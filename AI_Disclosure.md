# GenAI Disclosure

## Phase 2 (AI Level 2)
For REG requirements, GenAI was used to help us out in the placement of certain files within our predetermined system architecture. Also, GenAI was used to generate the code to register an account with a certain role, log in and log out of an account, authenticate account sessions and was also used to load seeded accounts for the demos. It was also used to ensure that the Facade design pattern was properly integrated. Finally, it was used to implement a H2 database system for our web application. It also updated the README file according to important changes.

For ORD requirements, GenAI was used to help implement the order lifecycle using the State pattern, and to record and publish order events using the Observer pattern. It also helped create the ORD application service, REST controller, in-memory order repository and temporary adapters for interfaces.

GenAI was also used to connect the REG implementation to the ORD implementation in various controllers such as AuthController, OrderController and MenuController.

For the Frontend of the project GenAI was used to create the user type specific page elements for the developed web page pattern. GenAi was also used to proofread, debug code and connect the different FrontEnd elements to their Backend implementation.
For the Customer and Restaurant specific pages, GenAi was used to help with some basic styling for certain elements.

## Phase 3 (AI Level 1)
For DLV, AI was used to get a roadmap of the tasks needed to implement the DLV module and to get a good understanding of what was needed from the other modules.
For Courier and Operator front end, AI was used to identify the different file dependencies and for troubleshooting.
For PRC, AI helped outline the tasks for implementing the PRC module and clarify what the module needed from the other components.
For solving merge conflicts, recovering files and finding bugs in the code.
For DASH, AI was used to help outline the files to modify and tasks to be done as well as for some debugging.

## Phase 4 (AI Level 2)
For PROMO, AI helped diagnose why rejected promo codes (e.g. an expired code) showed a generic "Bad Request" instead of the actual reason: Spring's default error response omits the exception message unless explicitly configured. AI added a global exception handler (`GlobalExceptionHandler`) that returns the real message for `CommandRejectedException`, `OrderNotFoundException`, and `IllegalArgumentException`/`IllegalStateException`, fixing this across the whole backend rather than just the promo case, and verified the fix live against the running app before confirming it. 