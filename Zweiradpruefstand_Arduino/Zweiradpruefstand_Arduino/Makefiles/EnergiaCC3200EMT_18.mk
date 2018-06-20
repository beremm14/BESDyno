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
# Last update: Jul 04, 2017 release 7.5.3






ENERGIA_CC3200_EMT_1    = $(ENERGIA_PACKAGES_PATH)/hardware/cc3200emt/$(ENERGIA_CC3200_EMT_RELEASE)

ifneq ($(wildcard $(ENERGIA_CC3200_EMT_1)),)
    ENERGIA_CC3200_EMT_APP    = $(ENERGIA_CC3200_EMT_1)
    ENERGIA_CC3200_EMT_PATH   = $(ENERGIA_PACKAGES_PATH)
    ENERGIA_18_CC3200_EMT_BOARDS = $(ENERGIA_CC3200_EMT_1)/boards.txt
endif

ifneq ($(call PARSE_FILE,$(BOARD_TAG_18),name,$(ENERGIA_18_CC1310_EMT_BOARDS)),)
MAKEFILE_NAME = EnergiaCC1300EMT_18


# Energia CC3200 EMT specifics
# ----------------------------------
#
PLATFORM         := Energia
BUILD_CORE       := cc3200
SUB_PLATFORM     := cc3200

PLATFORM_TAG      = ENERGIA=$(ENERGIA_RELEASE) ARDUINO=$(ARDUINO_RELEASE) EMBEDXCODE=$(RELEASE_NOW) ENERGIA_ARCH_CC3200EMT ENERGIA_$(call PARSE_BOARD,$(BOARD_TAG),build.board) ENERGIA_MT $(filter __%__ ,$(GCC_PREPROCESSOR_DEFINITIONS))

APPLICATION_PATH := $(ENERGIA_18_PATH)

#ENERGIA_RELEASE  := $(shell tail -c2 $(APPLICATION_PATH)/lib/version.txt)
#ARDUINO_RELEASE  := $(shell head -c4 $(APPLICATION_PATH)/lib/version.txt | tail -c3)
ENERGIA_RELEASE   := 10610
ARDUINO_RELEASE   := 10610
PLATFORM_VERSION := CC3200 EMT $(ENERGIA_CC3200_EMT_RELEASE) for Energia $(ENERGIA_RELEASE)
BOARD_TAG        := $(BOARD_TAG_18)

#ifeq ($(shell if [[ '$(ENERGIA_RELEASE)' -ge '17' ]] ; then echo 1 ; else echo 0 ; fi ),0)
#    WARNING_MESSAGE = Energia 17 or later is required.
#endif


HARDWARE_PATH     = $(ENERGIA_CC3200_EMT_PATH)/hardware/cc3200emt/$(ENERGIA_CC3200_EMT_RELEASE)
TOOL_CHAIN_PATH   = $(ENERGIA_CC3200_EMT_PATH)/tools/arm-none-eabi-gcc/$(ENERGIA_GCC_ARM_RELEASE)/bin
#OTHER_TOOLS_PATH  = $(ENERGIA_18_PATH)/hardware/tools/DSLite
OTHER_TOOLS_PATH  = $(ENERGIA_PACKAGES_PATH)/tools/dslite/$(ENERGIA_DSLITE_RELEASE)


# Uploader
# ----------------------------------
#
UPLOADER          = DSLite
UPLOADER_PATH     = $(OTHER_TOOLS_PATH)/DebugServer/bin
UPLOADER_EXEC     = $(UPLOADER_PATH)/DSLite
UPLOADER_OPTS     = $(OTHER_TOOLS_PATH)/cc3200.ccxml


# MSP432 requires a specific command
#
UPLOADER_COMMAND = prog

#APP_TOOLS_PATH  := $(APPLICATION_PATH)/hardware/tools/lm4f/bin

#CORE_LIB_PATH    := $(APPLICATION_PATH)/hardware/cc3200emt/cores/cc3200
CORES_PATH      := $(HARDWARE_PATH)/cores/cc3200emt
APP_LIB_PATH    := $(HARDWARE_PATH)/libraries
BOARDS_TXT      := $(HARDWARE_PATH)/boards.txt

#CORE_LIBS_LIST   := #
#BUILD_CORE_LIBS_LIST := #

#BUILD_CORE_LIB_PATH  = $(APPLICATION_PATH)/hardware/cc3200emt/cores/cc3200/driverlib
#BUILD_CORE_LIBS_LIST = $(subst .h,,$(subst $(BUILD_CORE_LIB_PATH)/,,$(wildcard $(BUILD_CORE_LIB_PATH)/*.h))) # */

#BUILD_CORE_C_SRCS    = $(wildcard $(BUILD_CORE_LIB_PATH)/*.c) # */

#BUILD_CORE_CPP_SRCS = $(filter-out %program.cpp %main.cpp,$(wildcard $(BUILD_CORE_LIB_PATH)/*.cpp)) # */

#BUILD_CORE_OBJ_FILES  = $(BUILD_CORE_C_SRCS:.c=.c.o) $(BUILD_CORE_CPP_SRCS:.cpp=.cpp.o)
#BUILD_CORE_OBJS       = $(patsubst $(BUILD_CORE_LIB_PATH)/%,$(OBJDIR)/%,$(BUILD_CORE_OBJ_FILES))


# Sketchbook/Libraries path
# ----------------------------------
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


BOARD            = $(call PARSE_BOARD,$(BOARD_TAG),board)
VARIANT          = $(call PARSE_BOARD,$(BOARD_TAG),build.variant)
VARIANT_PATH     = $(HARDWARE_PATH)/variants/$(VARIANT)
#CORE_A           = $(HARDWARE_PATH)/system/driverlib/MSP432P4xx/gcc/msp432p4xx_driverlib.a
LDSCRIPT         = $(HARDWARE_PATH)/cores/cc3200emt/ti/runtime/wiring/cc3200/linker.cmd


# Horrible patch for core libraries
# ----------------------------------
#
# If *_libdriverlib.a is available, exclude driverlib/
#
#CORE_LIB_PATH  = $(APPLICATION_PATH)/hardware/cc3200/cores/cc3200
CORE_LIB_PATH   := $(HARDWARE_PATH)/cores/cc3200emt

#CORE_A           = $(HARDWARE_PATH)/system/driverlib/MSP432P4xx/gcc/msp432p4xx_driverlib.a
CORE_A           = $(HARDWARE_PATH)/system/driverlib/gcc/exe/libdriver.a

BUILD_CORE_LIB_PATH = $(shell find $(CORE_LIB_PATH) -type d)
ifneq ($(wildcard $(CORE_A)),)
    BUILD_CORE_LIB_PATH := $(filter-out %/driverlib,$(BUILD_CORE_LIB_PATH))
endif

BUILD_CORE_CPP_SRCS = $(filter-out %program.cpp %main.cpp,$(foreach dir,$(BUILD_CORE_LIB_PATH),$(wildcard $(dir)/*.cpp))) # */
BUILD_CORE_C_SRCS   = $(foreach dir,$(BUILD_CORE_LIB_PATH),$(wildcard $(dir)/*.c)) # */
BUILD_CORE_H_SRCS   = $(foreach dir,$(BUILD_CORE_LIB_PATH),$(wildcard $(dir)/*.h)) # */

BUILD_CORE_OBJ_FILES  = $(BUILD_CORE_C_SRCS:.c=.c.o) $(BUILD_CORE_CPP_SRCS:.cpp=.cpp.o)
BUILD_CORE_OBJS       = $(patsubst $(HARDWARE_PATH)/%,$(OBJDIR)/%,$(BUILD_CORE_OBJ_FILES))

CORE_LIBS_LOCK = 1
# ----------------------------------


# Two locations for Energia libraries
# ----------------------------------
#
APP_LIB_PATH     = $(HARDWARE_PATH)/libraries

cc3200emt_00    = $(foreach dir,$(APP_LIB_PATH),$(patsubst %,$(dir)/%,$(APP_LIBS_LIST)))
cc3200emt_00   += $(foreach dir,$(APP_LIB_PATH),$(patsubst %,$(dir)/%/utility,$(APP_LIBS_LIST)))
cc3200emt_00   += $(foreach dir,$(APP_LIB_PATH),$(patsubst %,$(dir)/%/src,$(APP_LIBS_LIST)))
cc3200emt_00   += $(foreach dir,$(APP_LIB_PATH),$(patsubst %,$(dir)/%/src/utility,$(APP_LIBS_LIST)))
cc3200emt_00   += $(foreach dir,$(APP_LIB_PATH),$(patsubst %,$(dir)/%/src/arch/$(BUILD_CORE),$(APP_LIBS_LIST)))
cc3200emt_00   += $(foreach dir,$(APP_LIB_PATH),$(patsubst %,$(dir)/%/src/$(BUILD_CORE),$(APP_LIBS_LIST)))

APP_LIB_CPP_SRC = $(foreach dir,$(cc3200emt_00),$(wildcard $(dir)/*.cpp)) # */
APP_LIB_C_SRC   = $(foreach dir,$(cc3200emt_00),$(wildcard $(dir)/*.c)) # */
APP_LIB_H_SRC   = $(foreach dir,$(cc3200emt_00),$(wildcard $(dir)/*.h)) # */

APP_LIB_OBJS     = $(patsubst $(HARDWARE_PATH)/%.cpp,$(OBJDIR)/%.cpp.o,$(APP_LIB_CPP_SRC))
APP_LIB_OBJS    += $(patsubst $(HARDWARE_PATH)/%.c,$(OBJDIR)/%.c.o,$(APP_LIB_C_SRC))

BUILD_APP_LIB_PATH     = $(APPLICATION_PATH)/libraries

cc3200emt_10    = $(foreach dir,$(BUILD_APP_LIB_PATH),$(patsubst %,$(dir)/%,$(APP_LIBS_LIST)))
cc3200emt_10   += $(foreach dir,$(BUILD_APP_LIB_PATH),$(patsubst %,$(dir)/%/utility,$(APP_LIBS_LIST)))
cc3200emt_10   += $(foreach dir,$(BUILD_APP_LIB_PATH),$(patsubst %,$(dir)/%/src,$(APP_LIBS_LIST)))
cc3200emt_10   += $(foreach dir,$(BUILD_APP_LIB_PATH),$(patsubst %,$(dir)/%/src/utility,$(APP_LIBS_LIST)))
cc3200emt_10   += $(foreach dir,$(BUILD_APP_LIB_PATH),$(patsubst %,$(dir)/%/src/arch/$(BUILD_CORE),$(APP_LIBS_LIST)))
cc3200emt_10   += $(foreach dir,$(BUILD_APP_LIB_PATH),$(patsubst %,$(dir)/%/src/$(BUILD_CORE),$(APP_LIBS_LIST)))

BUILD_APP_LIB_CPP_SRC = $(foreach dir,$(cc3200emt_10),$(wildcard $(dir)/*.cpp)) # */
BUILD_APP_LIB_C_SRC   = $(foreach dir,$(cc3200emt_10),$(wildcard $(dir)/*.c)) # */
BUILD_APP_LIB_H_SRC   = $(foreach dir,$(cc3200emt_10),$(wildcard $(dir)/*.h)) # */

BUILD_APP_LIB_OBJS     = $(patsubst $(APPLICATION_PATH)/%.cpp,$(OBJDIR)/%.cpp.o,$(BUILD_APP_LIB_CPP_SRC))
BUILD_APP_LIB_OBJS    += $(patsubst $(APPLICATION_PATH)/%.c,$(OBJDIR)/%.c.o,$(BUILD_APP_LIB_C_SRC))

APP_LIBS_LOCK = 1
# ----------------------------------


OPTIMISATION   	 = -Os

MCU_FLAG_NAME    = mcpu
MCU              = $(firstword $(call PARSE_BOARD,$(BOARD_TAG),build.mcu))
F_CPU            = $(call PARSE_BOARD,$(BOARD_TAG),build.f_cpu)

SUB_PATH         = $(sort $(dir $(wildcard $(1)/*/))) # */

INCLUDE_PATH     = $(call SUB_PATH,$(CORES_PATH))
INCLUDE_PATH    += $(call SUB_PATH,$(VARIANT_PATH))
INCLUDE_PATH    += $(call SUB_PATH,$(APPLICATION_PATH)/hardware/common)
INCLUDE_PATH    += $(HARDWARE_PATH)/cores/cc3200emt
INCLUDE_PATH    += $(HARDWARE_PATH)/cores/cc3200
INCLUDE_PATH    += $(sort $(dir $(APP_LIB_H_SRC) $(BUILD_APP_LIB_H_SRC)))
INCLUDE_PATH    += $(HARDWARE_PATH)/system/driverlib
INCLUDE_PATH    += $(HARDWARE_PATH)/system/inc
INCLUDE_PATH    += $(HARDWARE_PATH)/system
INCLUDE_PATH    += $(HARDWARE_PATH)
INCLUDE_PATH    += $(HARDWARE_PATH)/cores/cc3200emt
INCLUDE_PATH    += $(HARDWARE_PATH)/cores/cc3200emt/ti/runtime/wiring
INCLUDE_PATH    += $(HARDWARE_PATH)/cores/cc3200emt/ti/runtime/wiring/cc3200
INCLUDE_PATH    += $(HARDWARE_PATH)/cores/cc3200emt/ti/runtime/wiring/cc3200/variants/$(VARIANT)

INCLUDE_LIBS    += Builds
INCLUDE_LIBS    += $(HARDWARE_PATH)/cores/cc3200emt
INCLUDE_LIBS    += $(HARDWARE_PATH)/cores/cc3200emt/ti/runtime/wiring/cc3200
INCLUDE_LIBS    += $(HARDWARE_PATH)/cores/cc3200emt/ti/runtime/wiring/cc3200/variants/$(VARIANT)
INCLUDE_LIBS    += $(HARDWARE_PATH)/cores/cc3200emt/gnu/targets/arm/libs/install-native/arm-none-eabi/lib/armv7e-m/softfp


# Flags for gcc, g++ and linker
# ----------------------------------
#
# Common CPPFLAGS for gcc, g++, assembler and linker
#
CPPFLAGS     = $(OPTIMISATION) $(WARNING_FLAGS)
CPPFLAGS    += -ffunction-sections -fdata-sections -mthumb -MMD -march=armv7e-m
CPPFLAGS    += -$(MCU_FLAG_NAME)=$(MCU) -DF_CPU=$(F_CPU) -mfloat-abi=soft -mabi=aapcs
CPPFLAGS    += -nostdlib -fno-threadsafe-statics --param max-inline-insns-single=500
CPPFLAGS    += $(addprefix -I, $(INCLUDE_PATH))
CPPFLAGS    += $(addprefix -D, $(PLATFORM_TAG)) -Dprintf=iprintf
CPPFLAGS    += -Dxdc_target_types__=gnu/targets/arm/std.h -Dxdc_target_name__=M4F
CPPFLAGS    += -Dxdc_cfg__xheader__='"configPkg/package/cfg/energia_pm4g.h"' -Dxdc__nolocalstring=1

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
LDFLAGS     += -Wl,--gc-sections -Wl,-u,main
LDFLAGS     += -mthumb -mfloat-abi=soft -mabi=aapcs -nostartfiles
LDFLAGS     += -$(MCU_FLAG_NAME)=$(MCU) -DF_CPU=$(F_CPU)
LDFLAGS     += -Wl,-T $(LDSCRIPT) $(CORE_A) $(addprefix -L, $(INCLUDE_LIBS))
LDFLAGS     += -lstdc++ -lgcc -lc -lm -lnosys


# Specific OBJCOPYFLAGS for objcopy only
# objcopy uses OBJCOPYFLAGS only
#
OBJCOPYFLAGS  = -v -Obinary

# Target
#
TARGET_HEXBIN = $(TARGET_ELF)


# Commands
# ----------------------------------
#
# Link command
#
COMMAND_LINK = $(CXX) $(OUT_PREPOSITION)$@ $(LOCAL_OBJS) $(TARGET_A) $(LDFLAGS)

# Upload command
#
COMMAND_UPLOAD = $(UPLOADER_EXEC) load -c $(UPLOADER_OPTS) -f $(TARGET_ELF)

endif
