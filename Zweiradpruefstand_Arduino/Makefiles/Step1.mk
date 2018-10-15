#
# embedXcode
# ----------------------------------
# Embedded Computing on Xcode
#
# Copyright © Rei VILO, 2010-2018
# http://embedxcode.weebly.com
# All rights reserved
#
#
# Last update: Jun 23, 2016 release 7.4.8






# Sketch unicity test and extension
# ----------------------------------
#
ifndef SKETCH_EXTENSION
    ifeq ($(words $(wildcard *.pde) $(wildcard *.ino)), 0)
        $(error No pde or ino sketch)
    endif

    ifneq ($(words $(wildcard *.pde) $(wildcard *.ino)), 1)
        $(error More than 1 pde or ino sketch)
    endif

    ifneq ($(wildcard *.pde),)
        SKETCH_EXTENSION := pde
    else ifneq ($(wildcard *.ino),)
        SKETCH_EXTENSION := ino
    else
        $(error Extension error)
    endif
endif

ifneq ($(MULTI_INO),1)
ifneq ($(SKETCH_EXTENSION),__main_cpp_only__)
    ifneq ($(SKETCH_EXTENSION),_main_cpp_only_)
        ifneq ($(SKETCH_EXTENSION),cpp)
            ifeq ($(words $(wildcard *.$(SKETCH_EXTENSION))), 0)
                $(error No $(SKETCH_EXTENSION) sketch)
            endif

            ifneq ($(words $(wildcard *.$(SKETCH_EXTENSION))), 1)
                $(error More than one $(SKETCH_EXTENSION) sketch)
            endif
        endif
    endif
endif
endif


# Board selection
# ----------------------------------
# Board specifics defined in .xconfig file
# BOARD_TAG and AVRDUDE_PORT 
#
ifneq ($(MAKECMDGOALS),boards)
    ifneq ($(MAKECMDGOALS),clean)
        ifndef BOARD_TAG
            $(error BOARD_TAG not defined)
        endif
    endif
endif

ifndef BOARD_PORT
    BOARD_PORT = /dev/tty.usb*
endif


# Path to applications folder
#
# $(HOME) same as $(wildcard ~)
# $(USER_PATH)/Library same as $(USER_LIBRARY_DIR)
#
USER_PATH      := $(HOME)
EMBEDXCODE_APP  = $(USER_LIBRARY_DIR)/embedXcode
PARAMETERS_TXT  = $(EMBEDXCODE_APP)/parameters.txt
USER_LIBRARY_DIR = $(USER_PATH)/Library
ARDUINO_180_LIBRARY_PATH  = $(HOME)/Library/Arduino15
include $(MAKEFILE_PATH)/About.mk
RELEASE_NOW = $(shell echo $(EMBEDXCODE_RELEASE) | sed 's/\.//g')

APPLICATIONS_PATH := /Applications


# APPlications full paths
# ----------------------------------
#
# Welcome unified 1.8.0 release for all Arduino.CC and Genuino, Arduino.ORG boards!
#
ifneq ($(wildcard $(APPLICATIONS_PATH)/Arduino.app),)
    ARDUINO_APP   := $(APPLICATIONS_PATH)/Arduino.app
endif

# Check release of Arduino.app 1.8.1 for all Arduino.CC and Genuino, Arduino.ORG boards
#
ifneq ($(wildcard $(APPLICATIONS_PATH)/Arduino.app),)
    ifneq ($(shell grep -e '$(ARDUINO_IDE_RELEASE)' $(APPLICATIONS_PATH)/Arduino.app/Contents/Java/lib/version.txt),)
        ARDUINO_180_APP = $(APPLICATIONS_PATH)/Arduino.app
    endif
endif

# Arduino
#
ARDUINO_PATH        := $(ARDUINO_APP)/Contents/Java
ARDUINO_180_PATH    := $(ARDUINO_180_APP)/Contents/Java

# Energia.app
#
ifneq ($(wildcard $(APPLICATIONS_PATH)/Energia.app),)
    ENERGIA_18_APP   = $(APPLICATIONS_PATH)/Energia.app
endif

ENERGIA_PACKAGES_PATH = $(HOME)/Library/Energia15/packages/energia
ENERGIA_18_PATH    = $(ENERGIA_18_APP)/Contents/Java

# Other boards with IDEs or plug-ins
# ----------------------------------
#
# . Teensyduino.app path
#
TEENSY_0    = $(APPLICATIONS_PATH)/Teensyduino.app
ifneq ($(wildcard $(TEENSY_0)),)
    TEENSY_APP    = $(TEENSY_0)
else
    TEENSY_APP    = $(ARDUINO_APP)
endif


# Check at least one IDE installed
#
ifeq ($(wildcard $(ARDUINO_180_APP)),)
ifeq ($(wildcard $(ESP8266_APP)),)
    ifeq ($(wildcard $(LINKIT_ARM_APP)),)
    ifeq ($(wildcard $(WIRING_APP)),)
    ifeq ($(wildcard $(ENERGIA_18_APP)),)
    ifeq ($(wildcard $(MAPLE_APP)),)
        ifeq ($(wildcard $(TEENSY_APP)),)
        ifeq ($(wildcard $(GLOWDECK_APP)),)
        ifeq ($(wildcard $(DIGISTUMP_APP)),)
        ifeq ($(wildcard $(MICRODUINO_APP)),)
        ifeq ($(wildcard $(LIGHTBLUE_APP)),)
            ifeq ($(wildcard $(INTEL_APP)),)
            ifeq ($(wildcard $(ROBOTIS_APP)),)
            ifeq ($(wildcard $(RFDUINO_APP)),)
            ifeq ($(wildcard $(REDBEARLAB_APP)),)
                ifeq ($(wildcard $(LITTLEROBOTFRIENDS_APP)),)
                ifeq ($(wildcard $(PANSTAMP_AVR_APP)),)
                ifeq ($(wildcard $(MBED_APP)/*),) # */
                ifeq ($(wildcard $(EDISON_YOCTO_APP)/*),) # */
                    ifeq ($(wildcard $(SPARK_APP)/*),) # */
                    ifeq ($(wildcard $(ADAFRUIT_AVR_APP)),)
                        $(error Error: no application found)
                    endif
                    endif
                endif
                endif
                endif
                endif
            endif
            endif
            endif
            endif
        endif
        endif
        endif
        endif
        endif
    endif
    endif
    endif
    endif
endif
endif


# Arduino-related nightmares
# ----------------------------------
#
# Get Arduino release
# Gone Arduino 1.0, 1.5 Java 6 and 1.5 Java 7 triple release nightmare
#
ifneq ($(wildcard $(ARDUINO_APP)),) # */
    s103 = $(ARDUINO_APP)/Contents/Java/lib/version.txt
    ARDUINO_RELEASE := $(shell cat $(s103) | sed -e "s/\.//g")
    ARDUINO_MAJOR := $(shell echo $(ARDUINO_RELEASE) | cut -d. -f 1-2)
else
    ARDUINO_RELEASE := 0
    ARDUINO_MAJOR   := 0
endif


# Miscellaneous
# ----------------------------------
# Variables
#
TARGET_NAME := embeddedcomputing
USER_FLAG   := false

# Builds directory
#
OBJDIR  = Builds

# Function PARSE_BOARD data retrieval from boards.txt
# result = $(call PARSE_BOARD 'boardname','parameter')
#
PARSE_BOARD = $(shell if [ -f $(BOARDS_TXT) ]; then grep ^$(1).$(2)= $(BOARDS_TXT) | cut -d = -f 2-; fi; )

# Function PARSE_FILE data retrieval from specified file
# result = $(call PARSE_FILE 'boardname','parameter','filename')
#
PARSE_FILE = $(shell if [ -f $(3) ]; then grep ^$(1).$(2) $(3) | cut -d = -f 2-; fi; )



# Clean if new BOARD_TAG
# ----------------------------------
#
NEW_TAG := $(strip $(OBJDIR)/$(BOARD_TAG).board) #
OLD_TAG := $(strip $(wildcard $(OBJDIR)/*.board)) # */

ifneq ($(OLD_TAG),$(NEW_TAG))
    CHANGE_FLAG := 1
else
    CHANGE_FLAG := 0
endif


# Identification and switch
# ----------------------------------
# Look if BOARD_TAG is listed as a Arduino/Arduino board
# Look if BOARD_TAG is listed as a Arduino/arduino/avr board *1.5
# Look if BOARD_TAG is listed as a Arduino/arduino/sam board *1.5
# Look if BOARD_TAG is listed as a chipKIT/PIC32 board
# Look if BOARD_TAG is listed as a Wiring/Wiring board
# Look if BOARD_TAG is listed as a Energia/MPS430 board
# Look if BOARD_TAG is listed as a MapleIDE/LeafLabs board
# Look if BOARD_TAG is listed as a Teensy/Teensy board
# Look if BOARD_TAG is listed as a Microduino/Microduino board
# Look if BOARD_TAG is listed as a Digistump/Digistump board
# Look if BOARD_TAG is listed as a IntelGalileo/arduino/x86 board
# Look if BOARD_TAG is listed as a Adafruit/Arduino board
# Look if BOARD_TAG is listed as a LittleRobotFriends board
# Look if BOARD_TAG is listed as a mbed board
# Look if BOARD_TAG is listed as a RedBearLab/arduino/RBL_nRF51822 board
# Look if BOARD_TAG is listed as a Spark board
#
# Order matters!
#
ifneq ($(MAKECMDGOALS),boards)
    ifneq ($(MAKECMDGOALS),clean)

        ifneq ($(ARDUINO_180_APP),)
            include $(MAKEFILE_PATH)/ArduinoAVR_180.mk
            include $(MAKEFILE_PATH)/ArduinoAVR_181.mk
            include $(MAKEFILE_PATH)/ArduinoSAM_180.mk
            include $(MAKEFILE_PATH)/ArduinoSAMD_180.mk

            include $(MAKEFILE_PATH)/chipKIT_180.mk
            include $(MAKEFILE_PATH)/ESP8266_180.mk
            include $(MAKEFILE_PATH)/ftDuino_180.mk
        endif

        ifneq ($(ENERGIA_18_APP),)
            include $(MAKEFILE_PATH)/EnergiaMSP430_18.mk
            include $(MAKEFILE_PATH)/EnergiaMSP430_19.mk
            include $(MAKEFILE_PATH)/EnergiaMSP432EMT_18.mk
            include $(MAKEFILE_PATH)/EnergiaTIVAC_18.mk
            include $(MAKEFILE_PATH)/EnergiaCC3200_18.mk
            include $(MAKEFILE_PATH)/EnergiaCC3200EMT_18.mk
        endif

        include $(MAKEFILE_PATH)/Teensy.mk

        ifeq ($(MAKEFILE_NAME),)
            ifneq (,$(wildcard $(UTILITIES_PATH)/embedXcode_prepare))
                $(info $(shell $(UTILITIES_PATH)/embedXcode_prepare))
            endif
            $(error $(BOARD_TAG) board is unknown)
        endif

    endif
endif

# Information on makefile
#
$(eval MAKEFILE_RELEASE = $(shell grep $(MAKEFILE_PATH)/$(MAKEFILE_NAME).mk -e '^# Last update' | rev | cut -d\  -f1-2 | rev ))

# List of sub-paths to be excluded
#
EXCLUDE_NAMES  = Example example Examples examples Archive archive Archives archives Documentation documentation Reference reference
EXCLUDE_NAMES += ArduinoTestSuite tests test .git linux
EXCLUDE_NAMES += $(EXCLUDE_LIBS)
EXCLUDE_LIST   = $(addprefix %,$(EXCLUDE_NAMES))

# Step 2
#
include $(MAKEFILE_PATH)/Step2.mk

