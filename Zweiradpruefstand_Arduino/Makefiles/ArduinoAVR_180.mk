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
# Last update: Nov 06, 2017 release 8.2.5


ARDUINO_180_PACKAGES_PATH = $(HOME)/Library/Arduino15/packages
ARDUINO_180_AVR_BOARDS = $(ARDUINO_180_PATH)/hardware/arduino/avr/boards.txt

BOARD_CHECK   := 0
ifneq ($(call PARSE_FILE,$(BOARD_TAG),name,$(ARDUINO_180_AVR_BOARDS)),)
    BOARD_CHECK   := 1
endif
ifneq ($(call PARSE_FILE,$(BOARD_TAG1),name,$(ARDUINO_180_AVR_BOARDS)),)
    BOARD_CHECK   := 1
endif

ifeq ($(BOARD_CHECK),1)
MAKEFILE_NAME = ArduinoAVR_180


# Adafruit 1.6.1 AVR specifics
# Arduino 1.6.1 AVR specifics
# LittleRobotFriends 1.6.1 specifics
# ----------------------------------
#
ifneq ($(findstring LITTLEROBOTFRIENDS,$(GCC_PREPROCESSOR_DEFINITIONS)),)
    PLATFORM         := LittleRobotFriends
    PLATFORM_TAG      = ARDUINO=$(LITTLEROBOTFRIENDS_RELEASE) EMBEDXCODE=$(RELEASE_NOW) $(GCC_PREPROCESSOR_DEFINITIONS)
    APPLICATION_PATH := $(LITTLEROBOTFRIENDS_PATH)
    BOARDS_TXT       := $(LITTLEROBOTFRIENDS_BOARDS)
    USER_LIBS_LIST   := $(filter-out 0,$(USER_LIBS_LIST)) LittleRobotFriends

else ifneq ($(findstring ADAFRUIT,$(GCC_PREPROCESSOR_DEFINITIONS)),)
    PLATFORM         := Adafruit
    PLATFORM_TAG      = ARDUINO=$(ARDUINO_RELEASE) ADAFRUIT EMBEDXCODE=$(RELEASE_NOW)
    APPLICATION_PATH := $(ARDUINO_PATH)
    BOARDS_TXT       := $(APPLICATION_PATH)/hardware/adafruit/avr/boards.txt

else
    PLATFORM         := Arduino
    PLATFORM_TAG      = ARDUINO=10803 ARDUINO_ARCH_AVR ARDUINO_$(BUILD_BOARD) EMBEDXCODE=$(RELEASE_NOW) ARDUINO_$(ARDUINO_TAG)
    APPLICATION_PATH := $(ARDUINO_PATH)
    BOARDS_TXT       := $(APPLICATION_PATH)/hardware/arduino/avr/boards.txt
endif

HARDWARE_PATH       := $(APPLICATION_PATH)/hardware/arduino/avr

APP_TOOLS_PATH      := $(APPLICATION_PATH)/hardware/tools/avr/bin
CORE_LIB_PATH       := $(HARDWARE_PATH)/cores/arduino
APP_LIB_PATH        := $(APPLICATION_PATH)/libraries
#BOARDS_TXT       := $(HARDWARE_PATH)/boards.txt
BUILD_BOARD         =  $(call SEARCH_FOR,$(BOARD_TAGS_LIST),build.board)

BUILD_CORE           = avr
SUB_PLATFORM         = avr
PLATFORM_VERSION    := 'AVR (native) for Arduino $(ARDUINO_IDE_RELEASE)'


# Sketchbook/Libraries path
# wildcard required for ~ management
# ?ibraries required for libraries and Libraries
#
ifeq ($(USER_LIBRARY_DIR)/Arduino15/preferences.txt,)
    $(error Error: run Arduino once and define the sketchbook path)
endif

ifeq ($(wildcard $(SKETCHBOOK_DIR)),)
    SKETCHBOOK_DIR = $(shell grep sketchbook.path $(USER_LIBRARY_DIR)/Arduino15/preferences.txt | cut -d = -f 2)
endif
ifeq ($(wildcard $(SKETCHBOOK_DIR)),)
   $(error Error: sketchbook path not found)
endif
USER_LIB_PATH  = $(wildcard $(SKETCHBOOK_DIR)/?ibraries)

# Rules for making a c++ file from the main sketch (.pde)
#
PDEHEADER      = \\\#include \"Arduino.h\"  

# Tool-chain names
#
CC      = $(APP_TOOLS_PATH)/avr-gcc
CXX     = $(APP_TOOLS_PATH)/avr-g++
# LTO requires avr-gcc-ar
AR      = $(APP_TOOLS_PATH)/avr-gcc-ar
OBJDUMP = $(APP_TOOLS_PATH)/avr-objdump
OBJCOPY = $(APP_TOOLS_PATH)/avr-objcopy
SIZE    = $(APP_TOOLS_PATH)/avr-size
NM      = $(APP_TOOLS_PATH)/avr-nm


# Complicated menu system for Arduino 1.5
# Another example of Arduino's quick and dirty job
#
BOARD_TAGS_LIST   = $(BOARD_TAG) $(BOARD_TAG1) $(BOARD_TAG2)
SEARCH_FOR        = $(strip $(foreach t,$(1),$(call PARSE_BOARD,$(t),$(2))))

ifeq ($(BOARD_NAME),)
    BOARD_NAME          := $(call SEARCH_FOR,$(BOARD_TAGS_LIST),name)
endif

# MCU options
#
MCU_FLAG_NAME           := mmcu
ifeq ($(MCU),)
    MCU                 := $(call SEARCH_FOR,$(BOARD_TAGS_LIST),build.mcu)
endif

ifeq ($(F_CPU),)
    F_CPU               := $(call SEARCH_FOR,$(BOARD_TAGS_LIST),build.f_cpu)
endif

ifeq ($(MAX_FLASH_SIZE),)
    MAX_FLASH_SIZE      := $(call SEARCH_FOR,$(BOARD_TAGS_LIST),upload.maximum_size)
endif

ifeq ($(AVRDUDE_BAUDRATE),)
    AVRDUDE_BAUDRATE    := $(call SEARCH_FOR,$(BOARD_TAGS_LIST),upload.speed)
endif

ifeq ($(AVRDUDE_PROGRAMMER),)
    AVRDUDE_PROGRAMMER  := $(call SEARCH_FOR,$(BOARD_TAGS_LIST),upload.protocol)
endif


# Specific AVRDUDE location and options
#
AVRDUDE_COM_OPTS  = -D -p$(MCU) -C$(AVRDUDE_CONF)

ifneq ($(BOARD_TAG1),)
# Adafruit Pro Trinket uses arduino:eightanaloginputs
    avr165_10    = $(call PARSE_BOARD,$(BOARD_TAG1),build.variant)
    VARIANT      = $(patsubst arduino:%,%,$(avr165_10))
    VARIANT_PATH = $(HARDWARE_PATH)/variants/$(VARIANT)
else
# Adafruit Pro Trinket uses arduino:eightanaloginputs
    avr165_10        = $(call PARSE_BOARD,$(BOARD_TAG),build.variant)
    VARIANT      = $(patsubst arduino:%,%,$(avr165_10))
    VARIANT_PATH = $(HARDWARE_PATH)/variants/$(VARIANT)
endif


# Two locations for Arduino libraries
#
APP_LIB_PATH     = $(APPLICATION_PATH)/libraries
APP_LIB_PATH    += $(APPLICATION_PATH)/hardware/arduino/$(BUILD_CORE)/libraries

avr165_20    = $(foreach dir,$(APP_LIB_PATH),$(patsubst %,$(dir)/%,$(APP_LIBS_LIST)))
avr165_20   += $(foreach dir,$(APP_LIB_PATH),$(patsubst %,$(dir)/%/utility,$(APP_LIBS_LIST)))
avr165_20   += $(foreach dir,$(APP_LIB_PATH),$(patsubst %,$(dir)/%/src,$(APP_LIBS_LIST)))
avr165_20   += $(foreach dir,$(APP_LIB_PATH),$(patsubst %,$(dir)/%/src/utility,$(APP_LIBS_LIST)))
avr165_20   += $(foreach dir,$(APP_LIB_PATH),$(patsubst %,$(dir)/%/src/arch/$(BUILD_CORE),$(APP_LIBS_LIST)))
avr165_20   += $(foreach dir,$(APP_LIB_PATH),$(patsubst %,$(dir)/%/src/$(BUILD_CORE),$(APP_LIBS_LIST)))

APP_LIB_CPP_SRC = $(foreach dir,$(avr165_20),$(wildcard $(dir)/*.cpp)) # */
APP_LIB_C_SRC   = $(foreach dir,$(avr165_20),$(wildcard $(dir)/*.c)) # */
APP_LIB_H_SRC   = $(foreach dir,$(avr165_20),$(wildcard $(dir)/*.h)) # */

APP_LIB_OBJS     = $(patsubst $(APPLICATION_PATH)/%.cpp,$(OBJDIR)/%.cpp.o,$(APP_LIB_CPP_SRC))
APP_LIB_OBJS    += $(patsubst $(APPLICATION_PATH)/%.c,$(OBJDIR)/%.c.o,$(APP_LIB_C_SRC))

#BUILD_APP_LIBS_LIST = $(subst $(BUILD_APP_LIB_PATH)/, ,$(APP_LIB_CPP_SRC))

APP_LIBS_LOCK = 1

CORE_C_SRCS     = $(wildcard $(CORE_LIB_PATH)/*.c $(CORE_LIB_PATH)/*/*.c) # */

avr166_30              = $(filter-out %main.cpp, $(wildcard $(CORE_LIB_PATH)/*.cpp $(CORE_LIB_PATH)/*/*.cpp $(CORE_LIB_PATH)/*/*/*.cpp $(CORE_LIB_PATH)/*/*/*/*.cpp)) # */
CORE_CPP_SRCS     = $(filter-out %/$(EXCLUDE_LIST),$(avr166_30))
CORE_AS1_SRCS        = $(shell find $(CORE_LIB_PATH) -name \*.S)
CORE_AS1_SRCS_OBJ = $(patsubst %.S,%.S.o,$(filter %S, $(CORE_AS1_SRCS)))
CORE_AS2_SRCS        = $(shell find $(CORE_LIB_PATH) -name \*.s)
CORE_AS2_SRCS_OBJ = $(patsubst %.s,%.s.o,$(filter %s, $(CORE_AS2_SRCS)))

CORE_OBJ_FILES  += $(CORE_C_SRCS:.c=.c.o) $(CORE_CPP_SRCS:.cpp=.cpp.o) $(CORE_AS1_SRCS_OBJ) $(CORE_AS2_SRCS_OBJ)
#    CORE_OBJS       += $(patsubst $(CORE_LIB_PATH)/%,$(OBJDIR)/%,$(CORE_OBJ_FILES))
#CORE_OBJS       += $(patsubst $(APPLICATION_PATH)/%,$(OBJDIR)/%,$(CORE_OBJ_FILES))
CORE_OBJS       += $(patsubst $(HARDWARE_PATH)/%,$(OBJDIR)/%,$(CORE_OBJ_FILES))

CORE_LIBS_LOCK = 1


# Arduino Leonardo USB PID VID
#
USB_VID     := $(call PARSE_BOARD,$(BOARD_TAG),build.vid)
USB_PID     := $(call PARSE_BOARD,$(BOARD_TAG),build.pid)
USB_VENDOR  := $(call PARSE_BOARD,$(BOARD_TAG),build.usb_manufacturer)
USB_PRODUCT := $(call PARSE_BOARD,$(BOARD_TAG),build.usb_product)

ifneq ($(USB_VID),)
    USB_FLAGS    = -DUSB_VID=$(USB_VID)
    USB_FLAGS   += -DUSB_PID=$(USB_PID)
    #USB_FLAGS   += -DUSBCON
    USB_FLAGS   += -DUSB_MANUFACTURER='$(USB_VENDOR)'
    USB_FLAGS   += -DUSB_PRODUCT='$(USB_PRODUCT)'
endif

# Arduino Leonardo serial 1200 reset
#
USB_TOUCH := $(call PARSE_BOARD,$(BOARD_TAG),upload.protocol)

ifeq ($(USB_TOUCH),avr109)
    USB_RESET  = python $(UTILITIES_PATH)/reset_1200.py
endif


INCLUDE_PATH    = $(CORE_LIB_PATH) $(APP_LIB_PATH) $(VARIANT_PATH)
INCLUDE_PATH   += $(sort $(dir $(APP_LIB_CPP_SRC) $(APP_LIB_C_SRC) $(APP_LIB_H_SRC)))
INCLUDE_PATH   += $(sort $(dir $(BUILD_APP_LIB_CPP_SRC) $(BUILD_APP_LIB_C_SRC)))

FIRST_O_IN_A     = $$(find . -name wiring_pulse.S.o)


# Flags for gcc, g++ and linker
# ----------------------------------
#
# Common CPPFLAGS for gcc, g++, assembler and linker
#
CPPFLAGS     = $(OPTIMISATION) $(WARNING_FLAGS)
CPPFLAGS    += -$(MCU_FLAG_NAME)=$(MCU) -DF_CPU=$(F_CPU)
CPPFLAGS    += -ffunction-sections -fdata-sections -flto -MMD
CPPFLAGS    += $(addprefix -D, printf=iprintf $(PLATFORM_TAG))
CPPFLAGS    += $(addprefix -I, $(INCLUDE_PATH))

# Specific CFLAGS for gcc only
# gcc uses CPPFLAGS and CFLAGS
#
CFLAGS       = -std=gnu11 -fno-fat-lto-objects

# Specific CXXFLAGS for g++ only
# g++ uses CPPFLAGS and CXXFLAGS
#
CXXFLAGS     = -fdata-sections -fno-threadsafe-statics -std=gnu++11 -fpermissive -fno-exceptions

# Specific ASFLAGS for gcc assembler only
# gcc assembler uses CPPFLAGS and ASFLAGS
#
ASFLAGS      = -x assembler-with-cpp

# Specific LDFLAGS for linker only
# linker uses CPPFLAGS and LDFLAGS
#
LDFLAGS      = $(OPTIMISATION) $(WARNING_FLAGS)
LDFLAGS     += -$(MCU_FLAG_NAME)=$(MCU) -Wl,--gc-sections

# Specific OBJCOPYFLAGS for objcopy only
# objcopy uses OBJCOPYFLAGS only
#
#OBJCOPYFLAGS  = -v -Oihex

# Target
#
TARGET_HEXBIN    = $(TARGET_HEX)
TARGET_EEP       = $(OBJDIR)/$(TARGET_NAME).eep


# Commands
# ----------------------------------
# Link command
#
COMMAND_LINK    = $(CC) $(OUT_PREPOSITION)$@ $(LOCAL_OBJS) $(TARGET_A) $(LDFLAGS) -LBuilds -lm

endif
