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
# Last update: Nov 02, 2017 release 8.2.4






ENERGIA_CC3200_1    = $(ENERGIA_PACKAGES_PATH)/hardware/cc3200/$(ENERGIA_CC3200_RELEASE)
ifneq ($(wildcard $(ENERGIA_CC3200_1)),)
    ENERGIA_CC3200_APP    = $(ENERGIA_CC3200_1)
    ENERGIA_CC3200_PATH   = $(ENERGIA_PACKAGES_PATH)
    ENERGIA_18_CC3200_BOARDS = $(ENERGIA_CC3200_1)/boards.txt
endif

ifneq ($(call PARSE_FILE,$(BOARD_TAG_18),name,$(ENERGIA_18_CC3200_BOARDS)),)
MAKEFILE_NAME = EnergiaCC3200_18

# Force release 4.8.4-20140725 for ARM GCC tool-chain
# Release 6.3.1-20170620 conflicts with itoa.h
ENERGIA_GCC_ARM_RELEASE := 4.8.4-20140725


# Energia LaunchPad CC3200 specifics
# ----------------------------------
#
ifneq ($(filter ARDUCAM,$(GCC_PREPROCESSOR_DEFINITIONS)),ARDUCAM)
    PLATFORM         := Energia
else
    PLATFORM         := ArduCAM
endif
BUILD_CORE       := cc3200
SUB_PLATFORM     := cc3200

PLATFORM_TAG      = ENERGIA=$(ENERGIA_RELEASE) ARDUINO=$(ARDUINO_RELEASE) EMBEDXCODE=$(RELEASE_NOW) ENERGIA_ARCH_CC3200 ENERGIA_$(call PARSE_BOARD,$(BOARD_TAG),build.board)
#$(filter __%__ ,$(GCC_PREPROCESSOR_DEFINITIONS))

APPLICATION_PATH := $(ENERGIA_18_PATH)

# Release check
# ----------------------------------
#
REQUIRED_ENERGIA_CC3200_RELEASE = 1.0.2
ifeq ($(shell if [[ '$(ENERGIA_CC3200_RELEASE)' > '$(REQUIRED_ENERGIA_CC3200_RELEASE)' ]] || [[ '$(ENERGIA_CC3200_RELEASE)' = '$(REQUIRED_ENERGIA_CC3200_RELEASE)' ]]; then echo 1 ; else echo 0 ; fi ),0)
$(error Energia CC3200 release $(REQUIRED_ENERGIA_CC3200_RELEASE) or later required, release $(ENERGIA_CC3200_RELEASE) installed)
endif


ENERGIA_RELEASE  := 10610
ARDUINO_RELEASE  := 10610
PLATFORM_VERSION := CC3200 $(ENERGIA_CC3200_RELEASE) for Energia $(ENERGIA_RELEASE)
BOARD_TAG        := $(BOARD_TAG_18)

HARDWARE_PATH     = $(ENERGIA_CC3200_PATH)/hardware/cc3200/$(ENERGIA_CC3200_RELEASE)
TOOL_CHAIN_PATH   = $(ENERGIA_CC3200_PATH)/tools/arm-none-eabi-gcc/$(ENERGIA_GCC_ARM_RELEASE)/bin
OTHER_TOOLS_PATH  = $(ENERGIA_PACKAGES_PATH)/tools/cc3200prog/$(ENERGIA_CC3200_PROG_RELEASE)

#ifeq ($(shell if [[ '$(ENERGIA_RELEASE)' -ge '14' ]] ; then echo 1 ; else echo 0 ; fi ),0)
#    WARNING_MESSAGE = Energia 14 or later is required.
#endif


# Uploader
# ----------------------------------
#
UPLOADER          = cc3200serial
UPLOADER_PATH = $(OTHER_TOOLS_PATH)
#ifneq ($(wildcard $(UPLOADER_PATH)/lm4f/bin/serial),)
#    UPLOADER_EXEC      = $(UPLOADER_PATH)/lm4f/bin/serial
#else
UPLOADER_EXEC      = $(UPLOADER_PATH)/cc3200prog
#endif
UPLOADER_OPTS = /dev/tty.usbserial-cc3200B


# StellarPad requires a specific command
#
UPLOADER_COMMAND = prog
BOARDS_TXT      := $(HARDWARE_PATH)/boards.txt

CORE_LIB_PATH   := $(HARDWARE_PATH)/cores/cc3200

## Not so clean Energia implementation for CC3200 non-EMT
## Take cores/cc3200 but not the sub-folders...
##
#CORE_LIBS_LIST   = $(subst .h,,$(subst $(CORE_LIB_PATH)/,,$(wildcard $(CORE_LIB_PATH)/*.h))) # */
#CORE_C_SRCS      = $(wildcard $(CORE_LIB_PATH)/*.c) # */
#CORE_CPP_SRCS    = $(filter-out %/$(EXCLUDE_LIST),$(wildcard $(CORE_LIB_PATH)/*.cpp)) # */
#CORE_OBJ_FILES   = $(CORE_C_SRCS:.c=.c.o) $(CORE_CPP_SRCS:.cpp=.cpp.o)
#CORE_OBJS        = $(patsubst $(CORE_LIB_PATH)/%,$(OBJDIR)/%,$(CORE_OBJ_FILES))
#
## ...except cores/cc3200/avr
##
#BUILD_CORE_LIB_PATH  = $(CORE_LIB_PATH)/avr
#BUILD_CORE_LIBS_LIST = $(subst .h,,$(subst $(BUILD_CORE_LIB_PATH)/,,$(wildcard $(BUILD_CORE_LIB_PATH)/*.h))) # */
#BUILD_CORE_C_SRCS    = $(wildcard $(BUILD_CORE_LIB_PATH)/*.c) # */
#BUILD_CORE_CPP_SRCS = $(filter-out %program.cpp %main.cpp,$(wildcard $(BUILD_CORE_LIB_PATH)/*.cpp)) # */
#BUILD_CORE_OBJ_FILES  = $(BUILD_CORE_C_SRCS:.c=.c.o) $(BUILD_CORE_CPP_SRCS:.cpp=.cpp.o)
#BUILD_CORE_OBJS       = $(patsubst $(BUILD_CORE_LIB_PATH)/%,$(OBJDIR)/%,$(BUILD_CORE_OBJ_FILES))
#
#CORE_LIBS_LOCK = 1
#


# Sketchbook/Libraries path
# wildcard required for ~ management
# ?ibraries required for libraries and Libraries
#
ifeq ($(USER_LIBRARY_DIR)/Energia15/preferences.txt,)
    $(error Error: run Energia once and define the sketchbook path)
endif

ifeq ($(wildcard $(SKETCHBOOK_DIR)),)
    SKETCHBOOK_DIR = $(shell grep sketchbook.path $(wildcard ~/Library/Energia15/preferences.txt) | cut -d = -f 2)
endif

ifeq ($(wildcard $(SKETCHBOOK_DIR)),)
    $(error Error: sketchbook path not found)
endif

USER_LIB_PATH  = $(wildcard $(SKETCHBOOK_DIR)/?ibraries)


# Rules for making a c++ file from the main sketch (.pde)
#
PDEHEADER      = \\\#include \"Energia.h\"


# Tool-chain names
#
CC      = $(TOOL_CHAIN_PATH)/arm-none-eabi-gcc
CXX     = $(TOOL_CHAIN_PATH)/arm-none-eabi-g++
AR      = $(TOOL_CHAIN_PATH)/arm-none-eabi-ar
OBJDUMP = $(TOOL_CHAIN_PATH)/arm-none-eabi-objdump
OBJCOPY = $(TOOL_CHAIN_PATH)/arm-none-eabi-objcopy
SIZE    = $(TOOL_CHAIN_PATH)/arm-none-eabi-size
NM      = $(TOOL_CHAIN_PATH)/arm-none-eabi-nm
# ~
GDB     = $(TOOL_CHAIN_PATH)/arm-none-eabi-gdb
# ~~


# Horrible patch for core libraries
# ----------------------------------
#
# If *_libdriverlib.a is available, exclude driverlib/
#
#CORE_LIB_PATH  = $(APPLICATION_PATH)/hardware/cc3200/cores/cc3200
CORE_LIB_PATH   := $(HARDWARE_PATH)/cores/cc3200

CORE_A   = $(HARDWARE_PATH)/system/driverlib/libdriverlib.a

BUILD_CORE_LIB_PATH = $(shell find $(CORE_LIB_PATH) -type d)
ifneq ($(wildcard $(CORE_A)),)
    BUILD_CORE_LIB_PATH := $(filter-out %/driverlib,$(BUILD_CORE_LIB_PATH))
endif

BUILD_CORE_CPP_SRCS = $(filter-out %program.cpp %main.cpp,$(foreach dir,$(BUILD_CORE_LIB_PATH),$(wildcard $(dir)/*.cpp))) # */
BUILD_CORE_C_SRCS   = $(foreach dir,$(BUILD_CORE_LIB_PATH),$(wildcard $(dir)/*.c)) # */

BUILD_CORE_OBJ_FILES  = $(BUILD_CORE_C_SRCS:.c=.c.o) $(BUILD_CORE_CPP_SRCS:.cpp=.cpp.o)
BUILD_CORE_OBJS       = $(patsubst $(HARDWARE_PATH)/%,$(OBJDIR)/%,$(BUILD_CORE_OBJ_FILES))

CORE_LIBS_LOCK = 1
# ----------------------------------


# Two locations for Energia libraries
# ----------------------------------
#
APP_LIB_PATH     = $(HARDWARE_PATH)/libraries

cc3200_00    = $(foreach dir,$(APP_LIB_PATH),$(patsubst %,$(dir)/%,$(APP_LIBS_LIST)))
cc3200_00   += $(foreach dir,$(APP_LIB_PATH),$(patsubst %,$(dir)/%/utility,$(APP_LIBS_LIST)))
cc3200_00   += $(foreach dir,$(APP_LIB_PATH),$(patsubst %,$(dir)/%/src,$(APP_LIBS_LIST)))
cc3200_00   += $(foreach dir,$(APP_LIB_PATH),$(patsubst %,$(dir)/%/src/utility,$(APP_LIBS_LIST)))
cc3200_00   += $(foreach dir,$(APP_LIB_PATH),$(patsubst %,$(dir)/%/src/arch/$(BUILD_CORE),$(APP_LIBS_LIST)))
cc3200_00   += $(foreach dir,$(APP_LIB_PATH),$(patsubst %,$(dir)/%/src/$(BUILD_CORE),$(APP_LIBS_LIST)))

APP_LIB_CPP_SRC = $(foreach dir,$(cc3200_00),$(wildcard $(dir)/*.cpp)) # */
APP_LIB_C_SRC   = $(foreach dir,$(cc3200_00),$(wildcard $(dir)/*.c)) # */
APP_LIB_H_SRC   = $(foreach dir,$(cc3200_00),$(wildcard $(dir)/*.h)) # */

APP_LIB_OBJS     = $(patsubst $(HARDWARE_PATH)/%.cpp,$(OBJDIR)/%.cpp.o,$(APP_LIB_CPP_SRC))
APP_LIB_OBJS    += $(patsubst $(HARDWARE_PATH)/%.c,$(OBJDIR)/%.c.o,$(APP_LIB_C_SRC))

BUILD_APP_LIB_PATH     = $(APPLICATION_PATH)/libraries

cc3200_10    = $(foreach dir,$(BUILD_APP_LIB_PATH),$(patsubst %,$(dir)/%,$(APP_LIBS_LIST)))
cc3200_10   += $(foreach dir,$(BUILD_APP_LIB_PATH),$(patsubst %,$(dir)/%/utility,$(APP_LIBS_LIST)))
cc3200_10   += $(foreach dir,$(BUILD_APP_LIB_PATH),$(patsubst %,$(dir)/%/src,$(APP_LIBS_LIST)))
cc3200_10   += $(foreach dir,$(BUILD_APP_LIB_PATH),$(patsubst %,$(dir)/%/src/utility,$(APP_LIBS_LIST)))
cc3200_10   += $(foreach dir,$(BUILD_APP_LIB_PATH),$(patsubst %,$(dir)/%/src/arch/$(BUILD_CORE),$(APP_LIBS_LIST)))
cc3200_10   += $(foreach dir,$(BUILD_APP_LIB_PATH),$(patsubst %,$(dir)/%/src/$(BUILD_CORE),$(APP_LIBS_LIST)))

BUILD_APP_LIB_CPP_SRC = $(foreach dir,$(cc3200_10),$(wildcard $(dir)/*.cpp)) # */
BUILD_APP_LIB_C_SRC   = $(foreach dir,$(cc3200_10),$(wildcard $(dir)/*.c)) # */
BUILD_APP_LIB_H_SRC   = $(foreach dir,$(cc3200_10),$(wildcard $(dir)/*.h)) # */

BUILD_APP_LIB_OBJS     = $(patsubst $(APPLICATION_PATH)/%.cpp,$(OBJDIR)/%.cpp.o,$(BUILD_APP_LIB_CPP_SRC))
BUILD_APP_LIB_OBJS    += $(patsubst $(APPLICATION_PATH)/%.c,$(OBJDIR)/%.c.o,$(BUILD_APP_LIB_C_SRC))

APP_LIBS_LOCK = 1
# ----------------------------------


BOARD    = $(call PARSE_BOARD,$(BOARD_TAG),board)
LDSCRIPT = $(call PARSE_BOARD,$(BOARD_TAG),build.ldscript)
VARIANT  = $(call PARSE_BOARD,$(BOARD_TAG),build.variant)
VARIANT_PATH = $(HARDWARE_PATH)/variants/$(VARIANT)

# ~
ifeq ($(MAKECMDGOALS),debug)
    OPTIMISATION   = -O0 -ggdb
else
    OPTIMISATION   = -Os
endif
# ~~

MCU_FLAG_NAME   = mcpu
MCU = $(firstword $(call PARSE_BOARD,$(BOARD_TAG),build.mcu))
F_CPU = $(call PARSE_BOARD,$(BOARD_TAG),build.f_cpu)

INCLUDE_PATH     = $(CORE_LIB_PATH)
INCLUDE_PATH    += $(sort $(dir $(APP_LIB_H_SRC) $(BUILD_APP_LIB_H_SRC)))
INCLUDE_PATH    += $(VARIANT_PATH)
INCLUDE_PATH    += $(APPLICATION_PATH)/hardware/tools/cc3200/include
INCLUDE_PATH    += $(HARDWARE_PATH)/system
INCLUDE_PATH    += $(HARDWARE_PATH)/system/inc
INCLUDE_PATH    += $(HARDWARE_PATH)/system/driverlib
INCLUDE_PATH    += $(CORE_LIB_PATH)
INCLUDE_PATH    += $(CORE_LIB_PATH)/inc
INCLUDE_PATH    += $(BUILD_APP_LIB_PATH)

# Required for correct sprintf()
#
FIRST_O_IN_A     = $$(find . -name wiring.c.o)


# Flags for gcc, g++ and linker
# ----------------------------------
#
# Common CPPFLAGS for gcc, g++, assembler and linker
#
CPPFLAGS     = $(OPTIMISATION) $(WARNING_FLAGS)
CPPFLAGS    += -ffunction-sections -fdata-sections -mthumb -MMD
CPPFLAGS    += -Dprintf=iprintf --param max-inline-insns-single=500
CPPFLAGS    += -$(MCU_FLAG_NAME)=$(MCU) -DF_CPU=$(F_CPU) -mfloat-abi=soft -mabi=aapcs
CPPFLAGS    += -nostdlib -fno-threadsafe-statics
CPPFLAGS    += $(addprefix -I, $(INCLUDE_PATH))
CPPFLAGS    += $(addprefix -D, $(PLATFORM_TAG))

# Specific CFLAGS for gcc only
# gcc uses CPPFLAGS and CFLAGS
#
CFLAGS       = -std=gnu11

# Specific CXXFLAGS for g++ only
# g++ uses CPPFLAGS and CXXFLAGS
#
CXXFLAGS    = -fno-rtti -fno-exceptions -std=gnu++11

# Specific ASFLAGS for gcc assembler only
# gcc assembler uses CPPFLAGS and ASFLAGS
#
ASFLAGS      = --asm_extension=S

# Specific LDFLAGS for linker only
# linker uses CPPFLAGS and LDFLAGS
#
LDFLAGS      = $(OPTIMISATION) $(WARNING_FLAGS)
LDFLAGS     += -nostartfiles -nostdlib -Wl,--gc-sections
LDFLAGS     += -T $(CORE_LIB_PATH)/$(LDSCRIPT)
LDFLAGS     += -Wl,--entry=ResetISR -mthumb
LDFLAGS     += -$(MCU_FLAG_NAME)=$(MCU) -DF_CPU=$(F_CPU)
LDFLAGS     += -Wl,--check-sections -Wl,--gc-sections #-Wl,--cref
LDFLAGS     += -Wl,--entry=ResetISR -Wl,--unresolved-symbols=report-all -Wl,--warn-common
##LDFLAGS     += $(addprefix -I, $(INCLUDE_PATH))
#LDFLAGS     += $(CORE_A) -LBuilds -lm -lc -lgcc -lm

# Specific OBJCOPYFLAGS for objcopy only
# objcopy uses OBJCOPYFLAGS only
#
OBJCOPYFLAGS  = -Obinary # -v

# Target
#
TARGET_HEXBIN = $(TARGET_BIN)


# Commands
# ----------------------------------
#
COMMAND_LINK = $(CXX) $(LDFLAGS) $(OUT_PREPOSITION)$@ $(SYSTEM_OBJS) $(LOCAL_OBJS) $(TARGET_A) $(CORE_A) -LBuilds -lm -lc -lgcc -lm

endif
