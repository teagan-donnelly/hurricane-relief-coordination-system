# hurricane-relief-coordination-system

The Hurricane Relief Coordination System is a Java-based project designed to help coordinate disaster response after a hurricane. The system connects people requesting aid with volunteers and relief coordinators, while keeping track of requests, assignments, shelters, resources, and other information needed during relief efforts.

This project is being developed by our five-person team, **Cache Money**, for CSCE 247: Software Engineering.

## Documents

All project documentation can be found in the [`docs`](docs/) folder.

- [Requirements Document](docs/requirements.pdf)
- [UML Class Diagram](docs/uml-class-diagram.pdf)
- [UML Sequence Diagram 1](docs/uml-sequence-diagram1.pdf)
- [UML Sequence Diagram 2](docs/uml-sequence-diagram2.pdf)

## JSON Data

The `json` folder contains the JSON files used to store project data. Each file contains at least two objects, and the fields are designed to match the information represented in the UML class diagram.

- [Aid Coordinators](json/aid-coordinators.json)
- [Aid Requests](json/aid-request.json)
- [Aid Requesters](json/aid-requestors.json)
- [Hurricane Events](json/hurricane-event.json)
- [Volunteers](json/volunteer.json)