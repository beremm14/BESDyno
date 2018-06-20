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
# Last update: Apr 10, 2017 release 7.3.4






# Serial port check and selection
# ----------------------------------
#
ifneq ($(PLATFORM),mbed)
    include $(MAKEFILE_PATH)/Avrdude.mk
endif

$(shell echo > $(UTILITIES_PATH)/serial.txt)

# Some utilities manage paths with spaces
#
CURRENT_DIR_SPACE    := $(shell pwd)
UTILITIES_PATH_SPACE := $(CURRENT_DIR_SPACE)/Utilities
EMBEDXCODE_REFERENCE = embedXcode

ifeq ($(AVRDUDE_NO_SERIAL_PORT),1)
#
else ifeq ($(UPLOADER),teensy_flash)
#    teensy uploader in charge
else ifneq ($(MAKECMDGOALS),boards)
    ifneq ($(MAKECMDGOALS),build)
    ifneq ($(MAKECMDGOALS),make)
    ifneq ($(MAKECMDGOALS),document)
    ifneq ($(MAKECMDGOALS),clean)
        ifneq ($(MAKECMDGOALS),distribute)
        ifneq ($(MAKECMDGOALS),info)
        ifneq ($(MAKECMDGOALS),depends)
        ifeq ($(AVRDUDE_PORT),)
            $(error Serial port not available)
        else
            $(shell ls $(BOARD_PORT) > $(UTILITIES_PATH)/serial.txt)
        endif
        endif
        endif
        endif
    endif
    endif
    endif
    endif
endif

ifndef UPLOADER
    UPLOADER = avrdude
endif

ifndef BOARD_NAME
    BOARD_NAME = $(call PARSE_BOARD,$(BOARD_TAG),name)
endif


# Functions
# ----------------------------------
#

# Function SHOW action target source
# result = $(shell echo 'action',$(BOARD_TAG),'target','source')
#
#SHOW  = @echo $(1)'\t'$(suffix $(3))$(suffix $(2))' < '$(suffix $(3))'\t'$(BOARD_TAG)'	'$(dir $(2))'	'$(notdir $(3))
#SHOW  = @echo $(1)'\t'$(2)
SHOW  = @printf '%-24s\t%s\r\n' $(1) $(2)


# Find version of the platform
#
ifneq ($(UNKNOWN_BOARD),1)
ifeq ($(PLATFORM_VERSION),)
ifneq ($(MAKECMDGOALS),boards)
ifneq ($(MAKECMDGOALS),clean)
    ifeq ($(PLATFORM),MapleIDE)
        PLATFORM_VERSION := $(shell cat $(APPLICATION_PATH)/lib/build-version.txt)
    else ifeq ($(PLATFORM),mbed)
        PLATFORM_VERSION := $(shell cat $(APPLICATION_PATH)/version.txt)
# ~
    else ifeq ($(PLATFORM),IntelEdisonYocto)
        PLATFORM_VERSION := $(shell cat $(APPLICATION_PATH)/version.txt)
    else ifeq ($(PLATFORM),IntelEdisonMCU)
        PLATFORM_VERSION := $(shell cat $(EDISONMCU_PATH)/version.txt)
    else ifeq ($(PLATFORM),BeagleBoneDebian)
        PLATFORM_VERSION := $(shell cat $(APPLICATION_PATH)/version.txt)
# ~~
    else ifeq ($(PLATFORM),Spark)
        PLATFORM_VERSION := $(shell cat $(SPARK_PATH)/version.txt)
    else
        PLATFORM_VERSION := $(shell cat $(APPLICATION_PATH)/lib/version.txt)
    endif
endif
endif
endif
endif


# CORE libraries
# ----------------------------------
#
ifndef CORE_LIB_PATH
    CORE_LIB_PATH = $(APPLICATION_PATH)/hardware/arduino/cores/arduino
endif

ifndef CORE_LIBS_LIST
    s205            = $(subst .h,,$(subst $(CORE_LIB_PATH)/,,$(wildcard $(CORE_LIB_PATH)/*.h $(CORE_LIB_PATH)/*/*.h))) # */
    CORE_LIBS_LIST  = $(subst $(CORE_LIB_PATH)/,,$(filter-out $(EXCLUDE_LIST),$(s205)))
endif


# List of sources
# ----------------------------------
#
# CORE sources
#
ifeq ($(CORE_LIBS_LOCK),)
ifdef CORE_LIB_PATH
    CORE_C_SRCS     = $(wildcard $(CORE_LIB_PATH)/*.c $(CORE_LIB_PATH)/*/*.c) # */
    
    s210              = $(filter-out %main.cpp, $(wildcard $(CORE_LIB_PATH)/*.cpp $(CORE_LIB_PATH)/*/*.cpp $(CORE_LIB_PATH)/*/*/*.cpp $(CORE_LIB_PATH)/*/*/*/*.cpp)) # */
    CORE_CPP_SRCS     = $(filter-out %/$(EXCLUDE_LIST),$(s210))
    CORE_AS1_SRCS_OBJ = $(patsubst %.S,%.S.o,$(filter %S, $(CORE_AS_SRCS)))
    CORE_AS2_SRCS_OBJ = $(patsubst %.s,%.s.o,$(filter %s, $(CORE_AS_SRCS)))

    CORE_OBJ_FILES  += $(CORE_C_SRCS:.c=.c.o) $(CORE_CPP_SRCS:.cpp=.cpp.o) $(CORE_AS1_SRCS_OBJ) $(CORE_AS2_SRCS_OBJ)
    CORE_OBJS       += $(patsubst $(APPLICATION_PATH)/%,$(OBJDIR)/%,$(CORE_OBJ_FILES))
endif
endif

# APPlication Arduino/chipKIT/Digistump/Energia/Maple/Microduino/Teensy/Wiring sources
#
ifndef APP_LIB_PATH
    APP_LIB_PATH  = $(APPLICATION_PATH)/libraries
endif

ifeq ($(APP_LIBS_LIST),)
    s201         = $(realpath $(sort $(dir $(wildcard $(APP_LIB_PATH)/*/*.h $(APP_LIB_PATH)/*/*/*.h)))) # */
    APP_LIBS_LIST = $(subst $(APP_LIB_PATH)/,,$(filter-out $(EXCLUDE_LIST),$(s201)))
endif

ifeq ($(APP_LIBS_LOCK),)
    ifndef APP_LIBS
    ifneq ($(APP_LIBS_LIST),0)
        s204       = $(patsubst %,$(APP_LIB_PATH)/%,$(APP_LIBS_LIST))
        APP_LIBS   = $(realpath $(sort $(dir $(foreach dir,$(s204),$(wildcard $(dir)/*.h $(dir)/*/*.h $(dir)/*/*/*.h)))))
    endif
    endif
endif

ifndef APP_LIB_OBJS
    FLAG = 1
    APP_LIB_C_SRC     = $(wildcard $(patsubst %,%/*.c,$(APP_LIBS))) # */
    APP_LIB_CPP_SRC   = $(wildcard $(patsubst %,%/*.cpp,$(APP_LIBS))) # */
    APP_LIB_AS1_SRC   = $(wildcard $(patsubst %,%/*.s,$(APP_LIBS))) # */
    APP_LIB_AS2_SRC   = $(wildcard $(patsubst %,%/*.S,$(APP_LIBS))) # */
    APP_LIB_OBJ_FILES = $(APP_LIB_C_SRC:.c=.c.o) $(APP_LIB_CPP_SRC:.cpp=.cpp.o) $(APP_LIB_AS1_SRC:.s=.s.o) $(APP_LIB_AS2_SRC:.S=.S.o)
    APP_LIB_OBJS      = $(patsubst $(APPLICATION_PATH)/%,$(OBJDIR)/%,$(APP_LIB_OBJ_FILES))
else
    FLAG = 0
endif

# USER sources
# wildcard required for ~ management
# ?ibraries required for libraries and Libraries
#
ifndef USER_LIB_PATH
    USER_LIB_PATH    = $(wildcard $(SKETCHBOOK_DIR)/?ibraries)
endif

ifndef USER_LIBS_LIST
	s202             = $(realpath $(sort $(dir $(wildcard $(USER_LIB_PATH)/*/*.h)))) # */
    USER_LIBS_LIST   = $(subst $(USER_LIB_PATH)/,,$(filter-out $(EXCLUDE_LIST),$(s202)))
endif

ifneq ($(MAKECMDGOALS),clean)
ifeq ($(USER_LIBS_LOCK),)
ifneq ($(USER_LIBS_LIST),0)
    s203             = $(patsubst %,$(USER_LIB_PATH)/%,$(USER_LIBS_LIST))
    EXCLUDE_LIST     = $(shell echo $(strip $(EXCLUDE_NAMES)) | sed "s/ /|/g" )
    USER_LIBS       := $(sort $(foreach dir,$(s203),$(shell find $(dir) -type d | egrep -v '$(EXCLUDE_LIST)' )))

    USER_LIB_CPP_SRC = $(foreach dir,$(USER_LIBS),$(wildcard $(dir)/*.cpp)) # */
    USER_LIB_C_SRC   = $(foreach dir,$(USER_LIBS),$(wildcard $(dir)/*.c)) # */
    USER_LIB_H_SRC   = $(foreach dir,$(USER_LIBS),$(wildcard $(dir)/*.h)) # */

    USER_OBJS        = $(patsubst $(USER_LIB_PATH)/%.cpp,$(OBJDIR)/user/%.cpp.o,$(USER_LIB_CPP_SRC))
    USER_OBJS       += $(patsubst $(USER_LIB_PATH)/%.c,$(OBJDIR)/user/%.c.o,$(USER_LIB_C_SRC))
endif
endif
endif


# LOCAL sources
#
LOCAL_LIB_PATH  = .
#LOCAL_LIB_PATH  = $(CURRENT_DIR)

s206             = $(sort $(dir $(wildcard $(LOCAL_LIB_PATH)/*/*.h))) # */
LOCAL_LIBS_LIST  = $(subst $(LOCAL_LIB_PATH)/,,$(filter-out $(EXCLUDE_LIST)/,$(s206))) # */

s207             = $(patsubst %,$(LOCAL_LIB_PATH)/%,$(LOCAL_LIBS_LIST))
s208             = $(sort $(dir $(foreach dir,$(s207),$(wildcard $(dir)/*.h $(dir)/*/*.h $(dir)/*/*/*.h))))
LOCAL_LIBS       = $(shell echo $(s208)' ' | sed 's://:/:g' | sed 's:/ : :g')

# Core main function check
s209             = $(wildcard $(patsubst %,%/*.cpp,$(LOCAL_LIBS))) $(wildcard $(LOCAL_LIB_PATH)/*.cpp) # */
LOCAL_CPP_SRCS   = $(filter-out %$(PROJECT_NAME_AS_IDENTIFIER).cpp, $(s209))

LOCAL_CC_SRCS    = $(wildcard $(patsubst %,%/*.cc,$(LOCAL_LIBS))) $(wildcard $(LOCAL_LIB_PATH)/*.cc) # */
LOCAL_C_SRCS     = $(wildcard $(patsubst %,%/*.c,$(LOCAL_LIBS))) $(wildcard $(LOCAL_LIB_PATH)/*.c) # */

# Use of implicit rule for LOCAL_PDE_SRCS
#
LOCAL_AS1_SRCS   = $(wildcard $(patsubst %,%/*.S,$(LOCAL_LIBS))) $(wildcard $(LOCAL_LIB_PATH)/*.S) # */
LOCAL_AS2_SRCS   = $(wildcard $(patsubst %,%/*.s,$(LOCAL_LIBS))) $(wildcard $(LOCAL_LIB_PATH)/*.s) # */

LOCAL_OBJ_FILES = $(LOCAL_C_SRCS:.c=.c.o) $(LOCAL_CPP_SRCS:.cpp=.cpp.o) $(LOCAL_PDE_SRCS:.$(SKETCH_EXTENSION)=.$(SKETCH_EXTENSION).o) $(LOCAL_CC_SRCS:.cc=.cc.o) $(LOCAL_AS1_SRCS:.S=.S.o) $(LOCAL_AS2_SRCS:.s=.s.o)
LOCAL_OBJS      = $(patsubst $(LOCAL_LIB_PATH)/%,$(OBJDIR)/%,$(filter-out %/$(PROJECT_NAME_AS_IDENTIFIER).o,$(LOCAL_OBJ_FILES)))

# All the objects
# ??? Does order matter?
#
ifeq ($(REMOTE_OBJS),)
    REMOTE_OBJS = $(sort $(CORE_OBJS) $(BUILD_CORE_OBJS) $(APP_LIB_OBJS) $(BUILD_APP_LIB_OBJS) $(VARIANT_OBJS) $(USER_OBJS))
endif
OBJS        = $(REMOTE_OBJS) $(LOCAL_OBJS)

# Dependency files
#
DEPS   = $(OBJS:.o=.d)


# Processor model and frequency
# ----------------------------------
#
ifndef MCU
    MCU   = $(call PARSE_BOARD,$(BOARD_TAG),build.mcu)
endif

ifndef F_CPU
    F_CPU = $(call PARSE_BOARD,$(BOARD_TAG),build.f_cpu)
endif

ifeq ($(OUT_PREPOSITION),)
    OUT_PREPOSITION = -o # end of line
endif


# Rules
# ----------------------------------
#
# Main targets
#
TARGET_A    = $(OBJDIR)/$(TARGET_NAME).a
TARGET_HEX  = $(OBJDIR)/$(TARGET_NAME).hex
TARGET_ELF  = $(OBJDIR)/$(TARGET_NAME).elf
TARGET_BIN  = $(OBJDIR)/$(TARGET_NAME).bin
TARGET_BIN2 = $(OBJDIR)/$(TARGET_NAME).bin2
TARGET_OUT  = $(OBJDIR)/$(TARGET_NAME).out
TARGET_DOT  = $(OBJDIR)/$(TARGET_NAME)
TARGET_TXT  = $(OBJDIR)/$(TARGET_NAME).txt
TARGETS     = $(OBJDIR)/$(TARGET_NAME).*
TARGET_MCU  = $(OBJDIR)/$(TARGET_NAME).mcu

ifndef TARGET_HEXBIN
    TARGET_HEXBIN = $(TARGET_HEX)
endif

ifndef TARGET_EEP
    TARGET_EEP    =
endif

# List of dependencies
#
DEP_FILE   = $(OBJDIR)/depends.mk

# Executables
#
REMOVE  = rm -r
MV      = mv -f
CAT     = cat
ECHO    = echo

# General arguments
#
SYS_INCLUDES  = $(patsubst %,-I%,$(APP_LIBS))
SYS_INCLUDES += $(patsubst %,-I%,$(BUILD_APP_LIBS))
SYS_INCLUDES += $(patsubst %,-I%,$(USER_LIBS))
SYS_INCLUDES += $(patsubst %,-I%,$(LOCAL_LIBS))

SYS_OBJS      = $(wildcard $(patsubst %,%/*.o,$(APP_LIBS))) # */
SYS_OBJS     += $(wildcard $(patsubst %,%/*.o,$(BUILD_APP_LIBS))) # */
SYS_OBJS     += $(wildcard $(patsubst %,%/*.o,$(USER_LIBS))) # */

WARNING_FLAGS = -w

ifeq ($(OPTIMISATION),)
    OPTIMISATION = -Os -g
endif

ifeq ($(CPPFLAGS),)
    CPPFLAGS      = -$(MCU_FLAG_NAME)=$(MCU) -DF_CPU=$(F_CPU)
    CPPFLAGS     += $(SYS_INCLUDES) -g $(OPTIMISATION) $(WARNING_FLAGS) -ffunction-sections -fdata-sections
    CPPFLAGS     += $(EXTRA_CPPFLAGS) -I$(CORE_LIB_PATH)
else
    CPPFLAGS     += $(SYS_INCLUDES)
endif

ifdef USB_FLAGS
    CPPFLAGS += $(USB_FLAGS)
endif    

ifdef USE_GNU99
    CFLAGS       += -std=gnu99
endif

SCOPE_FLAG  := -$(PLATFORM)

# CXX = flags for C++ only
# CPP = flags for both C and C++
#
ifeq ($(CXXFLAGS),)
    CXXFLAGS      = -fno-exceptions
else
    CXXFLAGS     += $(EXTRA_CXXFLAGS)
endif

ifeq ($(ASFLAGS),)
    ASFLAGS       = -$(MCU_FLAG_NAME)=$(MCU) -x assembler-with-cpp
endif

ifeq ($(LDFLAGS),)
    LDFLAGS       = -$(MCU_FLAG_NAME)=$(MCU) -Wl,--gc-sections $(OPTIMISATION) $(EXTRA_LDFLAGS)
endif

ifndef OBJCOPYFLAGS
    OBJCOPYFLAGS  = -O ihex -R .eeprom
endif

CONFIGURATIONS_PATH_SPACE := $(CURRENT_DIR_SPACE)/Configurations


# Build
# ----------------------------------
#
# 1- APP and BUILD_APP, CORE and VARIANT libraries
#
$(OBJDIR)/%.cpp.o: $(APPLICATION_PATH)/%.cpp
	$(call SHOW,"1.1-APPLICATION CPP",$@,$<)
	@mkdir -p $(dir $@)
	$(CXX) -c $(CPPFLAGS) $(CXXFLAGS) $< $(OUT_PREPOSITION)$@

$(OBJDIR)/%.c.o: $(APPLICATION_PATH)/%.c
	$(call SHOW,"1.2-APPLICATION C",$@,$<)
	@mkdir -p $(dir $@)
	$(CC) -c $(CPPFLAGS) $(CFLAGS) $< $(OUT_PREPOSITION)$@

$(OBJDIR)/%.s.o: $(APPLICATION_PATH)/%.s
	$(call SHOW,"1.3-APPLICATION AS",$@,$<)
	@mkdir -p $(dir $@)
	$(CC) -c $(CPPFLAGS) $(ASFLAGS) $< $(OUT_PREPOSITION)$@

$(OBJDIR)/%.S.o: $(APPLICATION_PATH)/%.S
	$(call SHOW,"1.4-APPLICATION AS",$@,$<)
	@mkdir -p $(dir $@)
	$(CC) -c $(CPPFLAGS) $(ASFLAGS) $< $(OUT_PREPOSITION)$@

$(OBJDIR)/%.d: $(APPLICATION_PATH)/%.c
	$(call SHOW,"1.5-APPLICATION D",$@,$<)

	@mkdir -p $(dir $@)
	$(CC) -MM $(CPPFLAGS) $(CFLAGS) $< -MF $@ -MT $(@:.d=.c.o)

$(OBJDIR)/%.d: $(APPLICATION_PATH)/%.cpp
	$(call SHOW,"1.6-APPLICATION D",$@,$<)

	@mkdir -p $(dir $@)
	$(CXX) -MM $(CPPFLAGS) $(CXXFLAGS) $< -MF $@ -MT $(@:.d=.cpp.o)

$(OBJDIR)/%.d: $(APPLICATION_PATH)/%.S
	$(call SHOW,"1.7-APPLICATION D",$@,$<)

	@mkdir -p $(dir $@)
	$(CC) -MM $(CPPFLAGS) $(ASFLAGS) $< -MF $@ -MT $(@:.d=.S.o)

$(OBJDIR)/%.d: $(APPLICATION_PATH)/%.s
	$(call SHOW,"1.8-APPLICATION D",$@,$<)

	@mkdir -p $(dir $@)
	$(CC) -MM $(CPPFLAGS) $(ASFLAGS) $< -MF $@ -MT $(@:.d=.s.o)


# 2- APP and BUILD_APP, CORE and VARIANT libraries
#
$(OBJDIR)/%.cpp.o: $(HARDWARE_PATH)/%.cpp
	$(call SHOW,"2.1-HARDWARE CPP",$@,$<)
	@mkdir -p $(dir $@)
	$(CXX) -c $(CPPFLAGS) $(CXXFLAGS) $< $(OUT_PREPOSITION)$@

$(OBJDIR)/%.c.o: $(HARDWARE_PATH)/%.c
	$(call SHOW,"2.2-HARDWARE C",$@,$<)
	@mkdir -p $(dir $@)
	$(CC) -c $(CPPFLAGS) $(CFLAGS) $< $(OUT_PREPOSITION)$@

$(OBJDIR)/%.s.o: $(HARDWARE_PATH)/%.s
	$(call SHOW,"2.3-HARDWARE AS",$@,$<)
	@mkdir -p $(dir $@)
	$(CC) -c $(CPPFLAGS) $(ASFLAGS) $< $(OUT_PREPOSITION)$@

$(OBJDIR)/%.S.o: $(HARDWARE_PATH)/%.S
	$(call SHOW,"2.4-HARDWARE AS",$@,$<)
	@mkdir -p $(dir $@)
	$(CC) -c $(CPPFLAGS) $(ASFLAGS) $< $(OUT_PREPOSITION)$@

$(OBJDIR)/%.d: $(HARDWARE_PATH)/%.c
	$(call SHOW,"2.5-HARDWARE D",$@,$<)

	@mkdir -p $(dir $@)
	$(CC) -MM $(CPPFLAGS) $(CFLAGS) $< -MF $@ -MT $(@:.d=.c.o)

$(OBJDIR)/%.d: $(HARDWARE_PATH)/%.cpp
	$(call SHOW,"2.6-HARDWARE D",$@,$<)

	@mkdir -p $(dir $@)
	$(CXX) -MM $(CPPFLAGS) $(CXXFLAGS) $< -MF $@ -MT $(@:.d=.cpp.o)

$(OBJDIR)/%.d: $(HARDWARE_PATH)/%.S
	$(call SHOW,"2.7-HARDWARE D",$@,$<)

	@mkdir -p $(dir $@)
	$(CC) -MM $(CPPFLAGS) $(ASFLAGS) $< -MF $@ -MT $(@:.d=.S.o)

$(OBJDIR)/%.d: $(HARDWARE_PATH)/%.s
	$(call SHOW,"2.8-HARDWARE D",$@,$<)

	@mkdir -p $(dir $@)
	$(CC) -MM $(CPPFLAGS) $(ASFLAGS) $< -MF $@ -MT $(@:.d=.s.o)


# 3- USER library sources
#
$(OBJDIR)/user/%.cpp.o: $(USER_LIB_PATH)/%.cpp
	$(call SHOW,"3.1-USER CPP",$@,$<)

	@mkdir -p $(dir $@)
	$(CXX) -c $(CPPFLAGS) $(CXXFLAGS) $< $(OUT_PREPOSITION)$@

$(OBJDIR)/user/%.c.o: $(USER_LIB_PATH)/%.c
	$(call SHOW,"3.2-USER C",$@,$<)

	@mkdir -p $(dir $@)
	$(CC) -c $(CPPFLAGS) $(CFLAGS) $< $(OUT_PREPOSITION)$@

$(OBJDIR)/user/%.d: $(USER_LIB_PATH)/%.cpp
	$(call SHOW,"3.3-USER CPP",$@,$<)

	@mkdir -p $(dir $@)
	$(CXX) -MM $(CPPFLAGS) $(CXXFLAGS) $< -MF $@ -MT $(@:.d=.cpp.o)

$(OBJDIR)/user/%.d: $(USER_LIB_PATH)/%.c
	$(call SHOW,"3.4-USER C",$@,$<)

	@mkdir -p $(dir $@)
	$(CC) -MM $(CPPFLAGS) $(CFLAGS) $< -MF $@ -MT $(@:.d=.c.o)

    
# 4- LOCAL sources
# .o rules are for objects, .d for dependency tracking
# 
$(OBJDIR)/%.c.o: %.c
	$(call SHOW,"4.1-LOCAL C",$@,$<)

	@mkdir -p $(dir $@)
	$(CC) -c $(CPPFLAGS) $(CFLAGS) $< $(OUT_PREPOSITION)$@

$(OBJDIR)/%.cc.o: %.cc
	$(call SHOW,"4.2-LOCAL CC",$@,$<)

	@mkdir -p $(dir $@)
	$(CXX) -c $(CPPFLAGS) $(CXXFLAGS) $< $(OUT_PREPOSITION)$@

$(OBJDIR)/%.cpp.o: 	%.cpp
	$(call SHOW,"4.3-LOCAL CPP",$@,$<)

	@mkdir -p $(dir $@)
	$(CXX) -c $(CPPFLAGS) $(CXXFLAGS) $< $(OUT_PREPOSITION)$@

$(OBJDIR)/%.S.o: %.S
	$(call SHOW,"4.4-LOCAL AS",$@,$<)

	@mkdir -p $(dir $@)
	$(CC) -c $(CPPFLAGS) $(ASFLAGS) $< $(OUT_PREPOSITION)$@

$(OBJDIR)/%.s.o: %.s
	$(call SHOW,"4.5-LOCAL AS",$@,$<)

	@mkdir -p $(dir $@)
	$(CC) -c $(CPPFLAGS) $(ASFLAGS) $< $(OUT_PREPOSITION)$@

$(OBJDIR)/%.d: %.c
	$(call SHOW,"4.6-LOCAL C",$@,$<)

	@mkdir -p $(dir $@)
	$(CC) -MM $(CPPFLAGS) $(CFLAGS) $< -MF $@ -MT $(@:.d=.c.o)

$(OBJDIR)/%.d: %.cpp
	$(call SHOW,"4.7-LOCAL CPP",$@,$<)

	@mkdir -p $(dir $@)
	$(CXX) -MM $(CPPFLAGS) $(CXXFLAGS) $< -MF $@ -MT $(@:.d=.cpp.o)

$(OBJDIR)/%.d: %.S
	$(call SHOW,"4.8-LOCAL AS",$@,$<)

	@mkdir -p $(dir $@)
	$(CC) -MM $(CPPFLAGS) $(ASFLAGS) $< -MF $@ -MT $(@:.d=.S.o)

$(OBJDIR)/%.d: %.s
	$(call SHOW,"4.9-LOCAL AS",$@,$<)

	@mkdir -p $(dir $@)
	$(CC) -MM $(CPPFLAGS) $(ASFLAGS) $< -MF $@ -MT $(@:.d=.s.o)


# 5- Link
# ----------------------------------
#
$(TARGET_ELF): 	$(OBJS)
		@echo "---- Link ---- "
		$(call SHOW,"5.1-ARCHIVE",$@,.)

ifneq ($(FIRST_O_IN_A),)
		$(AR) rcs $(TARGET_A) $(FIRST_O_IN_A)
endif
		$(AR) rcs $(TARGET_A) $(REMOTE_OBJS)

ifneq ($(EXTRA_COMMAND),)
		$(call SHOW,"5.2-COPY",$@,.)
		$(EXTRA_COMMAND)
endif

ifneq ($(COMMAND_LINK),)
		$(call SHOW,"5.3-LINK",$@,.)
		$(COMMAND_LINK)

else
		$(call SHOW,"5.4-LINK default",$@,.)
		$(CXX) $(OUT_PREPOSITION)$@ $(LOCAL_OBJS) $(TARGET_A) $(LDFLAGS)
endif



# 6- Final conversions
# ----------------------------------
#
$(OBJDIR)/%.hex: $(OBJDIR)/%.elf
	$(call SHOW,"6.1-COPY HEX",$@,$<)
	$(OBJCOPY) -Oihex -R .eeprom $< $@

$(OBJDIR)/%.bin: $(OBJDIR)/%.elf
	$(call SHOW,"6.3-COPY",$@,$<)
	$(OBJCOPY) -O binary $< $@

$(OBJDIR)/%.bin2: $(OBJDIR)/%.elf
	$(call SHOW,"6.4-COPY BIN",$@,$<)
	$(ESP_POST_COMPILE) -eo $(BOOTLOADER_ELF) -bo Builds/$(TARGET_NAME)_$(ADDRESS_BIN1).bin -bm $(OBJCOPYFLAGS) -bf $(BUILD_FLASH_FREQ) -bz $(BUILD_FLASH_SIZE) -bs .text -bp 4096 -ec -eo $< -bs .irom0.text -bs .text -bs .data -bs .rodata -bc -ec
	cp Builds/$(TARGET_NAME)_$(ADDRESS_BIN1).bin Builds/$(TARGET_NAME).bin

$(OBJDIR)/%.eep: $(OBJDIR)/%.elf
	$(call SHOW,"6.5-COPY EEP",$@,$<)
	-$(OBJCOPY) -O ihex -j .eeprom --set-section-flags=.eeprom=alloc,load --no-change-warnings --change-section-lma .eeprom=0 $< $@

$(OBJDIR)/%.lss: $(OBJDIR)/%.elf
	$(call SHOW,"6.6-COPY LSS",$@,$<)
	$(OBJDUMP) -h -S $< > $@

$(OBJDIR)/%.sym: $(OBJDIR)/%.elf
	$(call SHOW,"6.7-COPY SYM",$@,$<)
	$(NM) -n $< > $@

$(OBJDIR)/%.txt: $(OBJDIR)/%.out
	$(call SHOW,"6.8-COPY",$@,$<)
	echo ' -boot -sci8 -a $< -o $@'
	$(OBJCOPY) -boot -sci8 -a $< -o $@

$(OBJDIR)/%.mcu: $(OBJDIR)/%.elf
	$(call SHOW,"6.9-COPY MCU",$@,$<)
	@rm -f $(OBJDIR)/intel_mcu.*
	@cp $(OBJDIR)/embeddedcomputing.elf $(OBJDIR)/intel_mcu.elf
	@cd $(OBJDIR) ; export TOOLCHAIN_PATH=$(APP_TOOLS_PATH) ; $(UTILITIES_PATH)/generate_mcu.sh

$(OBJDIR)/%: $(OBJDIR)/%.elf
	$(call SHOW,"6.11-COPY",$@,$<)
	cp $< $@


# Size of file
# ----------------------------------
#
ifeq ($(TARGET_HEXBIN),$(TARGET_HEX))
    FLASH_SIZE = $(SIZE) --target=ihex --totals $(CURRENT_DIR)/$(TARGET_HEX) | grep TOTALS | awk '{t=$$3 + $$2} END {print t}'
    RAM_SIZE = $(SIZE) --totals $(CURRENT_DIR)/$(TARGET_ELF) | sed '1d' | awk '{t=$$3 + $$2} END {print t}'
else ifeq ($(TARGET_HEXBIN),$(TARGET_BIN))
    FLASH_SIZE = $(SIZE) --target=binary --totals $(CURRENT_DIR)/$(TARGET_BIN) | grep TOTALS | tr '\t' . | cut -d. -f2 | tr -d ' '
    RAM_SIZE = $(SIZE) --totals $(CURRENT_DIR)/$(TARGET_ELF) | sed '1d' | awk '{t=$$3 + $$2} END {print t}'

else ifeq ($(TARGET_HEXBIN),$(TARGET_BIN2))

    FLASH_SIZE = $(SIZE) --totals $(CURRENT_DIR)/$(TARGET_ELF) | sed '1d' | awk '{t=$$1 + $$2} END {print t}'
    RAM_SIZE = $(SIZE) --totals $(CURRENT_DIR)/$(TARGET_ELF) | sed '1d' | awk '{t=$$3 + $$2} END {print t}'

else ifeq ($(TARGET_HEXBIN),$(TARGET_TXT))
    FLASH_SIZE = cat Builds/embeddedcomputing.map | grep '^.text' | awk 'BEGIN { OFS = "" } {print "0x",$$4}' | xargs printf '%d'
    RAM_SIZE = cat Builds/embeddedcomputing.map | grep '^.ebss' | awk 'BEGIN { OFS = "" } {print "0x",$$4}' | xargs printf '%d'

else ifeq ($(TARGET_HEXBIN),$(TARGET_DOT))
    FLASH_SIZE = $(SIZE) --totals $(CURRENT_DIR)/$(TARGET_ELF) | sed '1d' | awk '{t=$$1} END {print t}'
    RAM_SIZE = $(SIZE) --totals $(CURRENT_DIR)/$(TARGET_ELF) | sed '1d' | awk '{t=$$3 + $$2} END {print t}'

else ifeq ($(TARGET_HEXBIN),$(TARGET_ELF))
    FLASH_SIZE = $(SIZE) --totals $(CURRENT_DIR)/$(TARGET_ELF) | sed '1d' | awk '{t=$$1} END {print t}'
    RAM_SIZE = $(SIZE) --totals $(CURRENT_DIR)/$(TARGET_ELF) | sed '1d' | awk '{t=$$3 + $$2} END {print t}'

else ifeq ($(TARGET_HEXBIN),$(TARGET_MCU))
    FLASH_SIZE = $(SIZE) --totals $(CURRENT_DIR)/$(TARGET_ELF) | sed '1d' | awk '{t=$$4} END {print t}'
    RAM_SIZE = $(SIZE) --totals $(CURRENT_DIR)/$(TARGET_ELF) | sed '1d' | awk '{t=$$4} END {print t}'
endif

ifeq ($(MAX_FLASH_SIZE),)
    MAX_FLASH_SIZE = $(firstword $(call PARSE_BOARD,$(BOARD_TAG),upload.maximum_size))
endif
ifeq ($(MAX_RAM_SIZE),)
    MAX_RAM_SIZE = $(call PARSE_BOARD,$(BOARD_TAG),upload.maximum_data_size)
endif
ifeq ($(MAX_RAM_SIZE),)
    MAX_RAM_SIZE = $(call PARSE_BOARD,$(BOARD_TAG),upload.maximum_ram_size)
endif

ifneq ($(MAX_FLASH_SIZE),)
    MAX_FLASH_BYTES   = 'bytes (of a '$(MAX_FLASH_SIZE)' byte maximum)'
else
    MAX_FLASH_BYTES   = bytes used
endif

ifneq ($(MAX_RAM_SIZE),)
    MAX_RAM_BYTES   = 'bytes (of a '$(MAX_RAM_SIZE)' byte maximum)'
else
    MAX_RAM_BYTES   = bytes used
endif


# Serial monitoring
# ----------------------------------
#
ifndef SERIAL_BAUDRATE
    SERIAL_BAUDRATE = 9600
endif

ifndef SERIAL_COMMAND
    SERIAL_COMMAND  = screen
endif

STARTCHRONO      = $(shell $(UTILITIES_PATH)/embedXcode_chrono)
STOPCHRONO       = $(shell $(UTILITIES_PATH)/embedXcode_chrono -s)

ifeq ($(PLATFORM),Energia)
    ifeq ($(BUILD_CORE),msp432)
        USED_SERIAL_PORT = $(shell cat $(UTILITIES_PATH)/serial.txt | head -1)
    else
        USED_SERIAL_PORT = $(shell cat $(UTILITIES_PATH)/serial.txt | tail -1)
    endif

else
    USED_SERIAL_PORT = $(firstword $(wildcard $(BOARD_PORT)))
endif


# Info for debugging
# ----------------------------------
#
# 0- Info
#
info:
		@if [ -f $(UTILITIES_PATH)/embedXcode_check ]; then $(UTILITIES_PATH)/embedXcode_check; fi
		@echo $(STARTCHRONO)
ifeq ($(UNKNOWN_BOARD),1)
		@echo .
		@echo ==== Info ====
		@echo 'ERROR	$(BOARD_TAG) board is unknown'
		@echo ==== Info done ====
		exit 2
endif

ifneq ($(MAKECMDGOALS),boards)
  ifneq ($(MAKECMDGOALS),clean)
		@echo .
		@echo ==== Info ====
		@echo ---- Project ----
		@echo 'Target		'$(MAKECMDGOALS)
		@echo 'Name		'$(PROJECT_NAME)' ('$(SKETCH_EXTENSION)')'

    ifneq ($PLATFORM_VERSION),)
		@echo ---- Platform ----
		@echo 'IDE			'$(PLATFORM)' version '$(PLATFORM_VERSION)
    endif

    ifneq ($(WARNING_MESSAGE),)
		@echo 'WARNING		$(WARNING_MESSAGE)'
    endif
    ifneq ($(INFO_MESSAGE),)
		@echo 'Information	$(INFO_MESSAGE)'
    endif

    ifneq ($(BUILD_CORE),)
		@echo 'Platform		'$(BUILD_CORE)
    endif

    ifneq ($(VARIANT),)
		@echo 'Variant		'$(VARIANT)
    endif

    ifneq ($(USB_VID),)
		@echo 'USB			VID = '$(USB_VID)', PID = '$(USB_PID)
    endif

		@echo ---- Board ----
		@echo 'Name		''$(BOARD_NAME)' ' ('$(BOARD_TAG)')'
    ifneq ($(F_CPU),)
		@echo 'MCU			'$(MCU)' at '$(F_CPU)
    else
		@echo 'MCU			'$(MCU)
    endif

		@echo 'Memory		Flash = '$(MAX_FLASH_SIZE)' bytes, RAM = '$(MAX_RAM_SIZE)' bytes'

		@echo ---- Port ----
		@echo 'Uploader		'$(UPLOADER)

    ifeq ($(UPLOADER),avrdude)
        ifeq ($(AVRDUDE_NO_SERIAL_PORT),1)
			@echo 'AVRdude   	no serial port'
        else
			@echo 'AVRdude    	'$(AVRDUDE_PORT)
        endif
        ifneq ($(AVRDUDE_PROGRAMMER),)
			@echo 'Programmer	'$(AVRDUDE_PROGRAMMER)
        endif
        ifneq ($(BOOTLOADER),)
			@echo 'Boot-loader	'$(BOOTLOADER)
        endif
    endif
    ifeq ($(UPLOADER),mspdebug)
		@echo 'Protocol    	'$(UPLOADER_PROTOCOL)
    endif

    ifeq ($(AVRDUDE_NO_SERIAL_PORT),1)
		@echo 'Serial   	  	no serial port'
    else
		@echo 'Serial   	  	'$(USED_SERIAL_PORT)
    endif

		@echo ---- Libraries ----
		@echo Core libraries from $(CORE_LIB_PATH) # | cut -d. -f1,2
		@echo . $(CORE_LIBS_LIST)

   ifneq ($(APP_LIBS_LIST),0)
		@echo Application libraries from $(basename $(APP_LIB_PATH)) # | cut -d. -f1,2
        ifneq ($(strip $(APP_LIBS_LIST)),)
			@echo . $(APP_LIBS_LIST)
        endif
        ifneq ($(BUILD_APP_LIBS_LIST),)
			@echo . $(sort $(BUILD_APP_LIBS_LIST))
        endif
    endif

    ifneq ($(USER_LIBS_LIST),0)
		@echo User libraries from $(SKETCHBOOK_DIR)
		@echo . $(USER_LIBS_LIST)
    endif

		@echo Local libraries from $(CURRENT_DIR)

    ifneq ($(wildcard $(LOCAL_LIB_PATH)/*.h),) # */
		@echo . $(subst .h,,$(notdir $(wildcard $(LOCAL_LIB_PATH)/*.h))) # */
    endif
    ifneq ($(strip $(LOCAL_LIBS_LIST)),)
		@echo . '$(LOCAL_LIBS_LIST) ' | sed 's/\/ / /g'
    endif
    ifeq ($(wildcard $(LOCAL_LIB_PATH)/*.h),) # */
        ifeq ($(strip $(LOCAL_LIBS_LIST)),)
			@echo . none
        endif
    endif

		@echo ---- Tools ----
		@defaults read /System/Library/PrivateFrameworks/ServerInformation.framework/Versions/A/Resources/English.lproj/SIMachineAttributes.plist $$(sysctl hw.model | cut -d: -f2) | grep marketingModel | cut -d\" -f2-3 | sed 's/\\//g'
		@echo $$(sw_vers -productName) $$(sw_vers -productVersion)' ('$$(sw_vers -buildVersion)')'
#		@echo Xcode $$(system_profiler SPDeveloperToolsDataType | grep "Version" | cut -d: -f2) $$(echo on Mac $$(system_profiler SPSoftwareDataType | grep "System Version" | cut -d: -f2))
#		@echo Mac $$(system_profiler SPSoftwareDataType | grep "System Version" | cut -d: -f2)
		@echo Xcode $$(system_profiler SPDeveloperToolsDataType | grep "Version" | cut -d: -f2)
#		@echo Xcode $(XCODE_VERSION_ACTUAL)' ('$(XCODE_PRODUCT_BUILD_VERSION)')' | sed "s/\( ..\)/\1\./"
#		@echo $(EMBEDXCODE_EDITION) $(EMBEDXCODE_RELEASE) | sed 's/[0-9]/&./g' | sed 's/.$$//'
#		@echo $(EMBEDXCODE_EDITION) release $$(printf '%06s' $(EMBEDXCODE_RELEASE) | fold -w2 | paste -sd. -)
		@echo Platform $(PLATFORM) $(PLATFORM_VERSION)
		@echo '. Board $(BOARD_NAME) ($(BOARD_TAG))'
		@echo '$(EMBEDXCODE_REFERENCE) release' $(EMBEDXCODE_RELEASE)
		@echo '. Template $(EMBEDXCODE_EDITION) release' $$(grep $(CURRENT_DIR)/Makefile -e 'Last update' | rev | cut -d' ' -f1 | rev)
		@echo '. Makefile' $(MAKEFILE_NAME) $(MAKEFILE_RELEASE)
		@if [ -f '$(CONFIGURATIONS_PATH_SPACE)/$(CONFIG_NAME).xcconfig' ] ; then echo '. Configuration $(CONFIG_NAME) release' $$(grep '$(CONFIGURATIONS_PATH_SPACE)/$(CONFIG_NAME).xcconfig' -e 'Last update' | rev | cut -d' ' -f1 | rev) ; fi
		@echo "Other"
		@if [ -f $(UTILITIES_PATH)/embedXcode_check ]; then echo '. '$$( $(UTILITIES_PATH)/embedXcode_check -v ) ; fi
		@if [ -f $(UTILITIES_PATH)/embedXcode_prepare ]; then echo '. '$$( $(UTILITIES_PATH)/embedXcode_prepare -v ) ; fi
		@if [ -f $(UTILITIES_PATH)/embedXcode_debug ]; then echo '. '$$( $(UTILITIES_PATH)/embedXcode_debug -v ) ; fi
ifeq ($(BUILD_CORE),c2000)
		@echo '. '$$( $(CC) -version | head -1 )
else
		@echo '. '$$( $(CC) --version | head -1 )
endif

		@echo ==== Info done ====
  endif
endif


# Release management
# ----------------------------------
#



# Rules
# ----------------------------------
#
all: 		info message_all clean compile reset raw_upload serial end_all prepare


build: 		info message_build clean compile end_build prepare


compile:	info message_compile $(OBJDIR) $(TARGET_HEXBIN) $(TARGET_EEP) size
		@echo $(BOARD_TAG) > $(NEW_TAG)


prepare:
		@if [ -f $(UTILITIES_PATH)/embedXcode_prepare ]; then echo "." ; $(UTILITIES_PATH)/embedXcode_prepare $(SCOPE_FLAG) "$(USER_LIB_PATH)"; fi;


$(OBJDIR):
		@echo "---- Build ---- "
		@mkdir $(OBJDIR)


$(DEP_FILE):	$(OBJDIR) $(DEPS)
		@echo "9-" $<
		@cat $(DEPS) > $(DEP_FILE)


upload:		message_upload reset raw_upload
		@echo "==== upload done ==== "


reset:
		@echo "---- Reset ---- "

		-screen -X kill
		-screen -wipe
		sleep 1

    ifeq ($(UPLOADER),stlink)

    else ifeq ($(UPLOADER),dfu-util)
		$(call SHOW,"9.1-RESET",$(UPLOADER_RESET))

		$(UPLOADER_RESET)
		@sleep 1
    endif

    ifdef USB_RESET
		$(call SHOW,"9.2-RESET",USB_RESET 1200)

		-stty -f $(AVRDUDE_PORT) 1200
#		$(USB_RESET) $(USED_SERIAL_PORT)
		@sleep 1
endif


raw_upload:
		@echo "---- Upload ---- "

ifeq ($(RESET_MESSAGE),1)
		$(call SHOW,"10.1-UPLOAD",$(UPLOADER))

		@osascript -e 'tell application "System Events" to display dialog "Press the RESET button on the board $(BOARD_NAME) and then click OK." buttons {"OK"} default button {"OK"} with icon POSIX file ("$(UTILITIES_PATH_SPACE)/TemplateIcon.icns") with title "embedXcode"'
# Give Mac OS X enough time for enumerating the USB ports
		@sleep 3
endif

ifneq ($(COMMAND_PREPARE),)
		$(call SHOW,"10.80-PREPARE",$(UPLOADER))

		$(COMMAND_PREPARE)
		$(COMMAND_PREPARE)
endif

ifneq ($(COMMAND_UPLOAD),)
		$(call SHOW,"10.90-UPLOAD",$(UPLOADER))

    ifneq ($(DELAY_BEFORE_UPLOAD),)
		sleep $(DELAY_BEFORE_UPLOAD)
    endif

		$(COMMAND_UPLOAD)

    ifneq ($(DELAY_AFTER_UPLOAD),)
		sleep $(DELAY_AFTER_UPLOAD)
    endif

# ~
else ifeq ($(UPLOADER),micronucleus)
		$(call SHOW,"10.3-UPLOAD",$(UPLOADER))


else ifeq ($(UPLOADER),izmir_tty)

		$(call SHOW,"10.5-UPLOAD",$(UPLOADER))

		bash $(UPLOADER_EXEC) $(UPLOADER_OPTS) $(TARGET_ELF) $(USED_SERIAL_PORT)

else ifeq ($(UPLOADER),micronucleus)
		$(call SHOW,"10.10-UPLOAD",$(UPLOADER))

		osascript -e 'tell application "System Events" to display dialog "Click OK and plug the Digispark board into the USB port." buttons {"OK"} with icon POSIX file ("$(UTILITIES_PATH)/TemplateIcon.icns") with title "embedXcode"'

		$(AVRDUDE_EXEC) $(AVRDUDE_COM_OPTS) $(AVRDUDE_OPTS) -P$(USED_SERIAL_PORT) -Uflash:w:$(TARGET_HEX):i

else ifeq ($(PLATFORM),RedBearLab)
		$(call SHOW,"10.11-UPLOAD",$(UPLOADER))
		sleep 2

		$(OBJCOPY) -O ihex -I binary $(TARGET_BIN) $(TARGET_HEX)
		$(AVRDUDE_EXEC) $(AVRDUDE_COM_OPTS) $(AVRDUDE_OPTS) -P$(USED_SERIAL_PORT) -Uflash:w:$(TARGET_HEX):i
		sleep 2

else ifeq ($(UPLOADER),avrdude)

  ifeq ($(AVRDUDE_SPECIAL),1)
		$(call SHOW,"10.12-UPLOAD",$(UPLOADER) $(AVRDUDE_PROGRAMMER))

        ifeq ($(AVR_FUSES),1)
            $(AVRDUDE_EXEC) -p$(AVRDUDE_MCU) -C$(AVRDUDE_CONF) -c$(AVRDUDE_PROGRAMMER) -e -U lock:w:$(ISP_LOCK_FUSE_PRE):m -U hfuse:w:$(ISP_HIGH_FUSE):m -U lfuse:w:$(ISP_LOW_FUSE):m -U efuse:w:$(ISP_EXT_FUSE):m
        endif
		$(AVRDUDE_EXEC) -p$(AVRDUDE_MCU) -C$(AVRDUDE_CONF) -c$(AVRDUDE_PROGRAMMER) $(AVRDUDE_OTHER_OPTIONS) -U flash:w:$(TARGET_HEX):i
        ifeq ($(AVR_FUSES),1)
            $(AVRDUDE_EXEC) -p$(AVRDUDE_MCU) -C$(AVRDUDE_CONF) -c$(AVRDUDE_PROGRAMMER) -U lock:w:$(ISP_LOCK_FUSE_POST):m
        endif

  else
		$(call SHOW,"10.13-UPLOAD",$(UPLOADER))

        ifeq ($(USED_SERIAL_PORT),)
			$(AVRDUDE_EXEC) $(AVRDUDE_COM_OPTS) $(AVRDUDE_OPTS) -Uflash:w:$(TARGET_HEX):i
        else
			$(AVRDUDE_EXEC) $(AVRDUDE_COM_OPTS) $(AVRDUDE_OPTS) -P$(USED_SERIAL_PORT) -Uflash:w:$(TARGET_HEX):i
        endif
    ifeq ($(AVRDUDE_PROGRAMMER),avr109)
		sleep 2
    endif

  endif

else ifeq ($(UPLOADER),bossac)
		$(call SHOW,"10.14-UPLOAD",$(UPLOADER))

		$(UPLOADER_EXEC) $(UPLOADER_OPTS) $(TARGET_BIN) -R

else ifeq ($(UPLOADER),openocd)
    ifneq ($(MAKECMDGOALS),debug)
		$(call SHOW,"10.15-UPLOAD",$(UPLOADER))

		$(UPLOADER_EXEC) $(UPLOADER_OPTS) -c "program $(TARGET_BIN) $(UPLOADER_COMMAND)"
    endif

else ifeq ($(UPLOADER),mspdebug)
		$(call SHOW,"10.16-UPLOAD",$(UPLOADER))

  ifeq ($(UPLOADER_PROTOCOL),tilib)
		cd $(UPLOADER_PATH); ./mspdebug $(UPLOADER_OPTS) "$(UPLOADER_COMMAND) $(CURRENT_DIR_SPACE)/$(TARGET_HEX)";

  else
		$(UPLOADER_EXEC) $(UPLOADER_OPTS) "$(UPLOADER_COMMAND) $(TARGET_HEX)"
  endif
        
else ifeq ($(UPLOADER),lm4flash)
		$(call SHOW,"10.17-UPLOAD",$(UPLOADER))
		-killall openocd
		$(UPLOADER_EXEC) $(UPLOADER_OPTS) $(TARGET_BIN)

else ifeq ($(UPLOADER),cc3200serial)
		$(call SHOW,"10.18-UPLOAD",$(UPLOADER))

		-killall openocd
		$(UPLOADER_EXEC) $(USED_SERIAL_PORT) $(TARGET_BIN)

else ifeq ($(UPLOADER),DSLite)
		$(call SHOW,"10.19-UPLOAD",$(UPLOADER))
		$(UPLOADER_EXEC) $(UPLOADER_OPTS) $(TARGET_ELF)

else ifeq ($(UPLOADER),serial_loader2000)
		$(call SHOW,"10.20-UPLOAD",$(UPLOADER))
		$(UPLOADER_EXEC) -f $(TARGET_TXT) $(UPLOADER_OPTS) -p $(USED_SERIAL_PORT)

else ifeq ($(UPLOADER),dfu-util)
		$(call SHOW,"10.21-UPLOAD",$(UPLOADER))
		$(UPLOADER_EXEC) $(UPLOADER_OPTS) -D $(TARGET_BIN) -R
		sleep 4

else ifeq ($(UPLOADER),teensy_flash)
		$(call SHOW,"10.22-UPLOAD",$(UPLOADER))
		$(TEENSY_POST_COMPILE) -file=$(basename $(notdir $(TARGET_HEX))) -path="$(CURRENT_DIR_SPACE)/Builds" -tools=$(abspath $(TEENSY_FLASH_PATH)) -board=$(call PARSE_BOARD,$(BOARD_TAG),build.board) -reboot
		sleep 2
		$(TEENSY_REBOOT)
		sleep 2

else ifeq ($(UPLOADER),lightblue_loader)
		$(call SHOW,"10.23-UPLOAD",$(UPLOADER))
		$(LIGHTBLUE_POST_COMPILE) -board="$(BOARD_TAG)" -tools="$(abspath $(LIGHTBLUE_FLASH_PATH))" -path="$(dir $(abspath $(TARGET_HEX)))" -file="$(basename $(notdir $(TARGET_HEX)))"
		sleep 2

else ifeq ($(UPLOADER),izmirdl)
		$(call SHOW,"10.24-UPLOAD",$(UPLOADER))
		bash $(UPLOADER_EXEC) $(UPLOADER_OPTS) $(TARGET_ELF) $(USED_SERIAL_PORT)

else ifeq ($(UPLOADER),spark_usb)
		$(call SHOW,"10.25-UPLOAD",$(UPLOADER))
		$(eval SPARK_NAME = $(shell $(UPLOADER_EXEC) -l | grep 'serial' | cut -d\= -f8 | sed 's/\"//g' | head -1))

		@if [ -z '$(SPARK_NAME)' ] ; then echo 'ERROR No DFU found' ; exit 1 ; fi
		@echo 'DFU found $(SPARK_NAME)'

		$(PREPARE_EXEC) $(PREPARE_OPTS) "$(CURRENT_DIR)/$(TARGET_BIN)"
		$(UPLOADER_EXEC) $(UPLOADER_OPTS) "$(CURRENT_DIR)/$(TARGET_BIN)"

else ifeq ($(UPLOADER),cp)
		$(call SHOW,"10.31-UPLOAD",$(UPLOADER))

		osascript -e 'tell application "Finder" to duplicate file POSIX file "$(CURRENT_DIR)/$(TARGET_HEXBIN)" to disk "$(USED_VOLUME_PORT:/Volumes/%=%)" with replacing'

# Waiting for USB enumeration
		@sleep 5

else ifeq ($(UPLOADER),stlink)
		$(call SHOW,"10.32-UPLOAD",$(UPLOADER))

		$(UPLOADER_PATH)/$(UPLOADER_EXEC) write $(CURRENT_DIR)/$(TARGET_BIN) $(UPLOADER_OPTS)

else ifeq ($(UPLOADER),BsLoader.jar)
	$(call SHOW,"10.33-UPLOAD",$(UPLOADER))

	$(UPLOADER_EXEC) $(TARGET_HEX) $(USED_SERIAL_PORT) $(UPLOADER_OPTS)

else ifeq ($(UPLOADER),esptool)
	$(call SHOW,"10.34-UPLOAD",$(UPLOADER))

	$(UPLOADER_EXEC) $(UPLOADER_OPTS) -cp $(USED_SERIAL_PORT) -ca 0x$(ADDRESS_BIN1) -cf $(BUILDS_PATH)/$(TARGET_NAME)_$(ADDRESS_BIN1).bin

else ifeq ($(UPLOADER),esptool.py)
	$(call SHOW,"10.35-UPLOAD",$(UPLOADER))

	$(UPLOADER_EXEC) $(UPLOADER_OPTS) --port $(USED_SERIAL_PORT) write_flash 0x00000 $(BUILDS_PATH)/$(TARGET_NAME)_00000.bin 0x$(ADDRESS_BIN2) $(BUILDS_PATH)/$(TARGET_NAME)_$(ADDRESS_BIN2).bin
else
		$(error No valid uploader)
endif

ifeq ($(POST_RESET_MESSAGE),1)
		$(call SHOW,"10.36-UPLOAD",$(UPLOADER))

		@osascript -e 'tell application "System Events" to display dialog "Press the RESET button on the board $(BOARD_NAME) and then click OK." buttons {"OK"} default button {"OK"} with icon POSIX file ("$(UTILITIES_PATH)/TemplateIcon.icns") with title "embedXcode"'
# Give Mac OS X enough time for enumerating the USB ports
		@sleep 3
endif

ispload:	$(TARGET_HEX)
		@echo "---- ISP upload ---- "
ifeq ($(UPLOADER),avrdude)
		$(call SHOW,"10.37-UPLOAD",$(UPLOADER))

		$(AVRDUDE_EXEC) $(AVRDUDE_COM_OPTS) $(AVRDUDE_ISP_OPTS) -e \
			-U lock:w:$(ISP_LOCK_FUSE_PRE):m \
			-U hfuse:w:$(ISP_HIGH_FUSE):m \
			-U lfuse:w:$(ISP_LOW_FUSE):m \
			-U efuse:w:$(ISP_EXT_FUSE):m
		$(AVRDUDE_EXEC) $(AVRDUDE_COM_OPTS) $(AVRDUDE_ISP_OPTS) -D \
			-U flash:w:$(TARGET_HEX):i
		$(AVRDUDE_EXEC) $(AVRDUDE_COM_OPTS) $(AVRDUDE_ISP_OPTS) \
			-U lock:w:$(ISP_LOCK_FUSE_POST):m
endif


serial:		reset
		@echo "---- Serial ---- "
ifeq ($(AVRDUDE_NO_SERIAL_PORT),1)
		@echo "The programmer provides no serial port"

else ifeq ($(UPLOADER),teensy_flash)
		$(call SHOW,"11.6-SERIAL",$(UPLOADER))
		osascript -e 'tell application "Terminal" to do script "$(SERIAL_COMMAND) $$(ls $(BOARD_PORT)) $(SERIAL_BAUDRATE)"'

else ifeq ($(UPLOADER),lightblue_loader)
		$(call SHOW,"11.7-SERIAL",$(UPLOADER))
		osascript -e 'tell application "Terminal" to do script "$(SERIAL_COMMAND) $$(ls $(BOARD_PORT)) $(SERIAL_BAUDRATE)"'

else
		$(call SHOW,"11.8-SERIAL",$(UPLOADER))
		osascript -e 'tell application "Terminal" to do script "$(SERIAL_COMMAND) $(USED_SERIAL_PORT) $(SERIAL_BAUDRATE)"'  -e 'tell application "Terminal" to activate'
endif


size:
		@echo '---- Size ----'
		@echo 'Estimated Flash: ' $(shell $(FLASH_SIZE)) $(MAX_FLASH_BYTES); echo;
		@echo 'Estimated SRAM:  ' $(shell $(RAM_SIZE)) $(MAX_RAM_BYTES); echo;
		@echo 'Elapsed time:    ' $(STOPCHRONO)

clean:
		@if [ ! -d $(OBJDIR) ]; then mkdir $(OBJDIR); fi
		@echo "nil" > $(OBJDIR)/nil
		@echo "---- Clean ----"
		-@rm -r $(OBJDIR)/* # */

changed:
		@echo "---- Clean changed ----"
ifeq ($(CHANGE_FLAG),1)
		@if [ ! -d $(OBJDIR) ]; then mkdir $(OBJDIR); fi
		@echo "nil" > $(OBJDIR)/nil
		@$(REMOVE) $(OBJDIR)/* # */
		@echo "Remove all"
else
		@for f in $(LOCAL_OBJS); do if [ -f $$f ] ; then rm $$f; fi; done
		@echo "Remove local only"
		@if [ -f $(OBJDIR)/$(TARGET_NAME).elf ] ; then rm $(OBJDIR)/$(TARGET_NAME).* ; fi ;
endif

depends:	$(DEPS)
		@echo "---- Depends ---- "
		@cat $(DEPS) > $(DEP_FILE)

boards:
		@echo .
		@echo "==== Boards ===="
		@ls -1 Configurations/ | sed 's/\(.*\)\..*/\1/'
		@echo "==== Boards done ==== "

message_all:
		@echo .
		@echo "==== All ===="

message_build:
		@echo .
		@echo "==== Build ===="

message_compile:
		@echo "---- Compile ----"

message_upload:
		@echo .
		@echo "==== Upload ===="

end_all:
		@echo "==== All done ==== "

end_build:
		@echo "==== Build done ==== "

.NOTPARALLEL:

.PHONY:	all boards build changed clean compile depends end_all end_build end_fast end_make fast info ispload make message_all message_build message_compile message_fast message_make message_upload prepare raw_upload reset serial size upload archive do_archive unarchive


