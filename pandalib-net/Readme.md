# pandacube-net

A TCP network library that uses the standard Java socket API, to ease the ommunication between the different processes
running the server network Pandacube.

It’s still in development (actually not touched since years), and it’s supposed to be a replacement for the old
`pandalib-network-api`. This module is then marked as Beta using the Google Guava annotation.

- Packet based communication
- Supports Request/Answer packets
- Uses binary packet id and data
* Input streams are handled in separate Threads