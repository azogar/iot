#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "contiki-net.h"
#include "dev/button-sensor.h"

#include "erbium.h"

#include "er-coap-13.h"

#include "dev/leds.h"
#define MAX_NUMBER 100

#define DEBUG 1
#if DEBUG
#define PRINTF(...) printf(__VA_ARGS__)
#define PRINT6ADDR(addr) PRINTF("[%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x]", ((uint8_t *)addr)[0], ((uint8_t *)addr)[1], ((uint8_t *)addr)[2], ((uint8_t *)addr)[3], ((uint8_t *)addr)[4], ((uint8_t *)addr)[5], ((uint8_t *)addr)[6], ((uint8_t *)addr)[7], ((uint8_t *)addr)[8], ((uint8_t *)addr)[9], ((uint8_t *)addr)[10], ((uint8_t *)addr)[11], ((uint8_t *)addr)[12], ((uint8_t *)addr)[13], ((uint8_t *)addr)[14], ((uint8_t *)addr)[15])
#define PRINTLLADDR(lladdr) PRINTF("[%02x:%02x:%02x:%02x:%02x:%02x]",(lladdr)->addr[0], (lladdr)->addr[1], (lladdr)->addr[2], (lladdr)->addr[3],(lladdr)->addr[4], (lladdr)->addr[5])
#else
#define PRINTF(...)
#define PRINT6ADDR(addr)
#define PRINTLLADDR(addr)
#endif


static int n_entrances = 0;
static const char* xml_handler = "<Message><Total>%d</Total></Message>";
static const char* xml_event_handler = "<Message><Type>Exit</Type><Total>%d</Total></Message>";


EVENT_RESOURCE(myresource, METHOD_GET, "sensors/button", "title=\"Event demo\";obs");

void
myresource_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{
  const char msg[strlen(xml_handler)+MAX_NUMBER];
  snprintf(msg, sizeof(msg), xml_handler, n_entrances);

  REST.set_header_content_type(response, REST.type.TEXT_XML);
  REST.set_header_etag(response, (uint8_t*) strlen(msg), 1);

  REST.set_response_payload(response, (uint8_t *)msg, strlen(msg));

}

void
myresource_event_handler(resource_t *r)
{
  PRINTF("Exit registered\n");
  n_entrances++;
  const char content[strlen(xml_event_handler)+MAX_NUMBER];
  
  coap_packet_t notification[1];
  coap_init_message(notification, COAP_TYPE_CON, REST.status.OK, 0 );
  coap_set_payload(notification, content, snprintf(content, sizeof(content), xml_event_handler, n_entrances));

  REST.notify_subscribers(r, content, notification);
}


PROCESS(rest_server_example, "Exit Server");
AUTOSTART_PROCESSES(&rest_server_example);

PROCESS_THREAD(rest_server_example, ev, data)
{
  PROCESS_BEGIN();

  PRINTF("Starting Exit Server\n");

  rest_init_engine();
  SENSORS_ACTIVATE(button_sensor);

  rest_activate_resource(&resource_myresource);
  rest_activate_event_resource(&resource_myresource);

  while(1) {
    PROCESS_WAIT_EVENT();
    if (ev == sensors_event && data == &button_sensor) {
      myresource_event_handler(&resource_myresource);
    }
  }
  while(1) {
    PROCESS_WAIT_EVENT();
  }

  PROCESS_END();
}
