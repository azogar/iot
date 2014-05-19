all: er-obs-event er-obs-periodic
# use this target explicitly if requried: er-plugtest-server

CONTIKI=../../..

# variable for this Makefile
# configure CoAP implementation (3|7|12|13) (er-coap-07 also supports CoAP draft 08)
WITH_COAP=13
REST=coap_rest_implementation

# linker optimizations
SMALL=1

# for some platforms
UIP_CONF_IPV6=1
# IPv6 make config disappeared completely
CFLAGS += -DUIP_CONF_IPV6=1

CFLAGS += -DPROJECT_CONF_H=\"project-conf.h\"

CFLAGS += -DWITH_COAP=13
CFLAGS += -DREST=coap_rest_implementation
CFLAGS += -DUIP_CONF_TCP=0
APPS += er-coap-13
APPS += erbium

TARGET_LIBFILES += -lm

include $(CONTIKI)/Makefile.include
