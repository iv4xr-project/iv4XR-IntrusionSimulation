<h1>Nuclear plant intrusion simulation</h1>

Verification of the defense strategy of a critical infrastructure (as a nuclear power plant) against the infiltration of activists.
The defense strategy will be modeled by a specific scenario in the CGE MAEV environment.

**Iv4xr interface :**

This project must be linked with aplib and lab-recruits-api (iv4xrDemo) because it uses their agents, tactics, goals, helperstructures, ...

The intrusionSimulation package resume the principle of sockets and type of requests to communication with the server (which is made in C++).
This enables to avoid wrapping classes between java and C++ or to make a specific interface dedicated to this wrapping.

Request types : DISCONNECT, START, PAUSE, RESTART, AGENTCOMMAND

The data exchange will be done by json on the same principle as lab-recruits demo.
In a second time, if this kind of exchange format slows down the process, we can consider the exchange of data by binary structure.

Config Env will contains general informations about socket configuration or paths.