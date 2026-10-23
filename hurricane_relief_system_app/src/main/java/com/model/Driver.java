package com.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

/**
 * 
 * @author Teagan Donnelly
 */
public class Driver {

    public static void main(String[] args) {
        Location home = new Location(32.7765, -79.9311,
            "123 Palmetto Lane", 0, "Charleston", "SC", "29401");

        ArrayList<String> emergencyContacts = new ArrayList<>();
        emergencyContacts.add("Emily Carter: 843-555-0148");
        ArrayList<String> pastRequests = new ArrayList<>();

        UUID fatherID = UUID.fromString("a4f0c713-6e82-4f35-9c17-138b91423101");
        UUID requestID = UUID.fromString("b5e1d824-7f93-4046-8d28-249ca2534202");

        AidRequester father = new AidRequester("Michael", "Carter", fatherID,
            "michael.carter@example.com", "843-555-0147", home,
            LocalDate.of(1987, 4, 12), "michaelcarter", "CarterDemo123",
            NotificationType.EMAIL, emergencyContacts, 3,
            AssistanceType.TRANSPORT, pastRequests);

        if (!DataWriter.saveRequester(father)) {
            System.out.println("Could not save Michael Carter's account.");
            return;
        }

        String comment = "Hurricane Scarlett has caused a power outage and rising floodwater. " +
            "Michael Carter, age 39, and Emily Carter, age 37, need transportation " +
            "for themselves and their daughter Sophie, age 8: three people total. " +
            "Sophie's asthma symptoms have worsened in the hot, humid house. " +
            "She needs medical assistance and a shelter with medical staff. " +
            "The family is bringing one dog, Buddy, and will not leave him behind. " +
            "Transportation and the shelter must accommodate Buddy. " +
            "Floodwater prevents the family from safely using their car. " +
            "Contact Michael at 843-555-0147 or michael.carter@example.com. " +
            "Emily can also be reached at 843-555-0148.";

        PersonalRequest request = new PersonalRequest(requestID, RequestType.PERSONAL,
            father, comment, AssistanceType.TRANSPORT, Urgency.URGENT_PRIORITY,
            RequestStatus.PENDING_REVIEW, home, "", 3,
            "Rising floodwater surrounds the home; the family cannot safely use their car.",
            1, 1);

        if (DataWriter.saveRequest(request)) {
            System.out.println("Michael Carter's account and relief request were saved.");
            System.out.println("Location: 123 Palmetto Lane, Charleston, SC 29401");
            System.out.println(comment);
        } else {
            System.out.println("Account saved, but the relief request could not be saved.");
        }

        public static void registerRosa() {
        Location rosaHome = new Location(32.7843, -79.9389,
            "58 Rutledge Avenue", 0, "Charleston", "SC", "29401");

        ArrayList<String> emergencyContacts = new ArrayList<>();
        emergencyContacts.add("Luis Delgado: 843-555-0172");

        ArrayList<String> skills = new ArrayList<>();
        skills.add("Transportation");

        ArrayList<String> supplies = new ArrayList<>();
        supplies.add("Ford F-350 truck");

        Volunteer rosa = new Volunteer("Rosa", "Delgado",
            UUID.fromString("c6f2e935-8a04-4157-9e39-35adb3645303"),
            "rosa.delgado@example.com", "843-555-0171", rosaHome,
            LocalDate.of(1984, 6, 3), "rosadelgado", "RosaDemo123",
            NotificationType.SMS, emergencyContacts,
            true, 25.0, skills, new ArrayList<Credential>(), supplies,
            true, false, 0.0);

        if (DataWriter.saveVolunteer(rosa)) {
            System.out.println("Rosa Delgado was registered as a volunteer.");
            System.out.println("Specialty: Transportation (Ford F-350 truck)");
        } else {
            System.out.println("Could not save Rosa Delgado's volunteer account.");
        }
    }

    }
}
