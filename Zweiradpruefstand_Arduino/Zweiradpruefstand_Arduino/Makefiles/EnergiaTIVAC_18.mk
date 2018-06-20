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
# Last update: May 10, 2017 release 7.4.2






ENERGIA_TIVAC_1    = $(ENERGIA_PACKAGES_PATH)/hardware/tivac/$(ENERGIA_TIVAC_RELEASE)

ifneq ($(wildcard $(ENERGIA_TIVAC_1)),)
    ENERGIA_TIVAC_APP    = $(ENERGIA_TIVAC_1)
    ENERGIA_TIVAC_PATH   = $(ENERGIA_PACKAGES_PATH)
    ENERGIA_18_TIVAC_BOARDS = $(ENERGIA_TIVAC_1)/boards.txt
endif

ifneq ($(call PARSE_FILE,$(BOARD_TAG_18),name,$(ENERGIA_18_TIVAC_BOARDS)),)
MAKEFILE_NAME = EnergiaTIVAC_18


# Energia LaunchPad Stellaris and Tiva C specifics
# ----------------------------------
#
PLATFORM         := Energia
BUILD_CORE       := tm4c
SUB_PLATFORM     := tm4c

PLATFORM_TAG      = ENERGIA=$(ENERGIA_RELEASE) ARDUINO=$(ARDUINO_RELEASE) EMBEDXCODE=$(RELEASE_NOW) ENERGIA_ARCH_TIVAC ENERGIA_$(call PARSE_BOARD,$(BOARD_TAG),build.board)
#$(filter __%__ ,$(GCC_PREPROCESSOR_DEFINITIONS))

APPLICATION_PATH := $(ENERGIA_18_PATH)

#ENERGIA_RELEASE  := $(shell tail -c2 $(APPLICATION_PATH)/lib/version.txt)
#ARDUINO_RELEASE  := $(shell head -c4 $(APPLICATION_PATH)/lib/version.txt | tail -c3)
ENERGIA_RELEASE   := 10610
ARDUINO_RELEASE   := 10610
PLATFORM_VERSION := TivaC $(ENERGIA_TIVAC_RELEASE) for Energia $(ENERGIA_RELEASE)
BOARD_TAG        := $(BOARD_TAG_18)

HARDWARE_PATH     = $(ENERGIA_TIVAC_PATH)/hardware/tivac/$(ENERGIA_TIVAC_RELEASE)
TOOL_CHAIN_PATH   = $(ENERGIA_TIVAC_PATH)/tools/arm-none-eabi-gcc/$(ENERGIA_GCC_ARM_RELEASE)/bin

OTHER_TOOLS_PATH  = $(ENERGIA_18_PATH)/hardware/tools/DSLite


# Uploader
# ----------------------------------
#
UPLOADER          = DSLite
UPLOADER_PATH     = $(OTHER_TOOLS_PATH)/DebugServer/bin
UPLOADER_EXEC     = $(UPLOADER_PATH)/DSLite
UPLOADER_OPTS     = $(OTHER_TOOLS_PATH)/$(VARIANT).ccxml

# 1. Previous uploader = lm4flash
#
#UPLOADER          = lm4flash
#UPLOADER_PATH     = $(APPLICATION_PATH)/hardware/tools
#UPLOADER_EXEC     = $(UPLOADER_PATH)/lm4f/bin/lm4flash
#UPLOADER_OPTS     =
#
# 2. OpenOCD as alternative to lm4flash
#
# openocd -f Utilities/debug_LM4F120XL.cfg -c "program Builds/embeddedcomputing.bin verify reset"
# openocd -f board/ek-lm4f120xl.cfg -c "program Builds/embeddedcomputing.bin verify reset"

# StellarPad requires a specific command
#
#UPLOADER_COMMAND = prog

#CORE_LIB_PATH    := $(APPLICATION_PATH)/hardware/lm4f/cores/lm4f
#APP_LIB_PATH     := $(APPLICATION_PATH)/hardware/lm4f/libraries
CORE_LIB_PATH     = $(HARDWARE_PATH)/cores/tivac
BOARDS_TXT       := $(HARDWARE_PATH)/boards.txt
BOARD_NAME        = $(call PARSE_BOARD,$(BOARD_TAG),build.board)


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
# If driverlib/libdriverlib.a is available, exclude driverlib/
#
CORE_LIB_PATH  = $(HARDWARE_PATH)/cores/tivac

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

msp432_00    = $(foreach dir,$(APP_LIB_PATH),$(patsubst %,$(dir)/%,$(APP_LIBS_LIST)))
msp432_00   += $(foreach dir,$(APP_LIB_PATH),$(patsubst %,$(dir)/%/utility,$(APP_LIBS_LIST)))
msp432_00   += $(foreach dir,$(APP_LIB_PATH),$(patsubst %,$(dir)/%/src,$(APP_LIBS_LIST)))
msp432_00   += $(foreach dir,$(APP_LIB_PATH),$(patsubst %,$(dir)/%/src/utility,$(APP_LIBS_LIST)))
msp432_00   += $(foreach dir,$(APP_LIB_PATH),$(patsubst %,$(dir)/%/src/arch/$(BUILD_CORE),$(APP_LIBS_LIST)))
msp432_00   += $(foreach dir,$(APP_LIB_PATH),$(patsubst %,$(dir)/%/src/$(BUILD_CORE),$(APP_LIBS_LIST)))

APP_LIB_CPP_SRC = $(foreach dir,$(msp432_00),$(wildcard $(dir)/*.cpp)) # */
APP_LIB_C_SRC   = $(foreach dir,$(msp432_00),$(wildcard $(dir)/*.c)) # */
APP_LIB_H_SRC   = $(foreach dir,$(msp432_00),$(wildcard $(dir)/*.h)) # */

APP_LIB_OBJS     = $(patsubst $(HARDWARE_PATH)/%.cpp,$(OBJDIR)/%.cpp.o,$(APP_LIB_CPP_SRC))
APP_LIB_OBJS    += $(patsubst $(HARDWARE_PATH)/%.c,$(OBJDIR)/%.c.o,$(APP_LIB_C_SRC))

BUILD_APP_LIB_PATH     = $(APPLICATION_PATH)/libraries

tivac_10    = $(foreach dir,$(BUILD_APP_LIB_PATH),$(patsubst %,$(dir)/%,$(APP_LIBS_LIST)))
tivac_10   += $(foreach dir,$(BUILD_APP_LIB_PATH),$(patsubst %,$(dir)/%/utility,$(APP_LIBS_LIST)))
tivac_10   += $(foreach dir,$(BUILD_APP_LIB_PATH),$(patsubst %,$(dir)/%/src,$(APP_LIBS_LIST)))
tivac_10   += $(foreach dir,$(BUILD_APP_LIB_PATH),$(patsubst %,$(dir)/%/src/utility,$(APP_LIBS_LIST)))
tivac_10   += $(foreach dir,$(BUILD_APP_LIB_PATH),$(patsubst %,$(dir)/%/src/arch/$(BUILD_CORE),$(APP_LIBS_LIST)))
tivac_10   += $(foreach dir,$(BUILD_APP_LIB_PATH),$(patsubst %,$(dir)/%/src/$(BUILD_CORE),$(APP_LIBS_LIST)))

BUILD_APP_LIB_CPP_SRC = $(foreach dir,$(tivac_10),$(wildcard $(dir)/*.cpp)) # */
BUILD_APP_LIB_C_SRC   = $(foreach dir,$(tivac_10),$(wildcard $(dir)/*.c)) # */
BUILD_APP_LIB_H_SRC   = $(foreach dir,$(tivac_10),$(wildcard $(dir)/*.h)) # */

BUILD_APP_LIB_OBJS     = $(patsubst $(APPLICATION_PATH)/%.cpp,$(OBJDIR)/%.cpp.o,$(BUILD_APP_LIB_CPP_SRC))
BUILD_APP_LIB_OBJS    += $(patsubst $(APPLICATION_PATH)/%.c,$(OBJDIR)/%.c.o,$(BUILD_APP_LIB_C_SRC))

APP_LIBS_LOCK = 1
# ----------------------------------


BOARD    = $(call PARSE_BOARD,$(BOARD_TAG),board)
LDSCRIPT = $(call PARSE_BOARD,$(BOARD_TAG),build.ldscript)
VARIANT  = $(call PARSE_BOARD,$(BOARD_TAG),build.variant)
VARIANT_PATH = $(HARDWARE_PATH)/variants/$(VARIANT)


OPTIMISATION   = -Os

MCU_FLAG_NAME   = mcpu
MCU = $(firstword $(call PARSE_BOARD,$(BOARD_TAG),build.mcu))
F_CPU = $(call PARSE_BOARD,$(BOARD_TAG),build.f_cpu)


INCLUDE_PATH     = $(VARIANT_PATH)
INCLUDE_PATH    += $(sort $(dir $(APP_LIB_H_SRC) $(BUILD_APP_LIB_H_SRC)))
INCLUDE_PATH    += $(HARDWARE_PATH)/system
INCLUDE_PATH    += $(HARDWARE_PATH)/system/inc
INCLUDE_PATH    += $(HARDWARE_PATH)/system/driverlib
INCLUDE_PATH    += $(CORE_LIB_PATH)
INCLUDE_PATH    += $(CORE_LIB_PATH)/inc
INCLUDE_PATH    += $(BUILD_APP_LIB_PATH)


# Flags for gcc, g++ and linker
# ----------------------------------
#
# Common CPPFLAGS for gcc, g++, assembler and linker
#
CPPFLAGS     = $(OPTIMISATION) $(WARNING_FLAGS)
CPPFLAGS    += -ffunction-sections -fdata-sections -mthumb -MMD
CPPFLAGS    += -$(MCU_FLAG_NAME)=$(MCU) -DF_CPU=$(F_CPU)
CPPFLAGS    += -mfloat-abi=hard -mfpu=fpv4-sp-d16 -mabi=aapcs -fsingle-precision-constant
CPPFLAGS    += $(addprefix -I, $(INCLUDE_PATH))
CPPFLAGS    += $(addprefix -D, $(PLATFORM_TAG))

# Specific CFLAGS for gcc only
# gcc uses CPPFLAGS and CFLAGS
#
CFLAGS       = -std=gnu11

# Specific CXXFLAGS for g++ only
# g++ uses CPPFLAGS and CXXFLAGS
#
CXXFLAGS    = -fno-exceptions -fno-rtti -std=gnu++11

# Specific ASFLAGS for gcc assembler only
# gcc assembler uses CPPFLAGS and ASFLAGS
#
ASFLAGS      = --asm_extension=S

# Specific LDFLAGS for linker only
# linker uses CPPFLAGS and LDFLAGS
#
LDFLAGS      = $(OPTIMISATION) $(WARNING_FLAGS)
LDFLAGS     += -$(MCU_FLAG_NAME)=$(MCU) -DF_CPU=$(F_CPU)
LDFLAGS     += $(addprefix -I, $(INCLUDE_PATH))
#LDFLAGS     += $(addprefix -D, $(PLATFORM_TAG))
LDFLAGS     += -mfloat-abi=hard -mfpu=fpv4-sp-d16 -fsingle-precision-constant
LDFLAGS     += -Wl,--gc-sections
LDFLAGS     += -T $(VARIANT_PATH)/$(LDSCRIPT)
LDFLAGS     += -mthumb -nostdlib -nostartfiles
LDFLAGS     += -Wl,--check-sections -Wl,--gc-sections -Wl,--entry=ResetISR
LDFLAGS     += -Wl,--unresolved-symbols=report-all -Wl,--warn-common -Wl,--warn-section-align
#-Wl,--cref
LIB_FLAGS     = -L$(OBJDIR) $(CORE_A) -lm -lc -lgcc

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
# Link command
#
COMMAND_LINK = $(CXX) $(LDFLAGS) $(OUT_PREPOSITION)$@ $(SYSTEM_OBJS) $(LOCAL_OBJS) $(TARGET_A) $(LIB_FLAGS)

# Upload command
#
COMMAND_UPLOAD = $(UPLOADER_EXEC) load -c $(UPLOADER_OPTS) -f $(TARGET_ELF)

# Specific OBJCOPYFLAGS for objcopy only
# objcopy uses OBJCOPYFLAGS only
#
OBJCOPYFLAGS  = -v -Obinary

# Target
#
TARGET_HEXBIN = $(TARGET_BIN)

endif
