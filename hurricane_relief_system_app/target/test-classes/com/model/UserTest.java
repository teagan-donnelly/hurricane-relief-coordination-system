package com.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class UserTest{
   @Test 
   public void generateUniqueUserID(){
    Volunteer user1 = new Volunteer("John", "Doe", null, "john@email.com", "123456789", null, null, "johndoe", "doe123");
    Volunteer user2 = new Volunteer("Jane", "Doe", null, "jane@email.com", "987654321", null, null, "janedoe", "doe123");
    assertNotNull(user1.getUserId());
    assertNotNull(user2.getUserId());
    assertNotEquals(user1.getUserId(), user2.getUserId());
   } 


   @Test 
   public void saveUserId(){
    Volunteer user = new Volunteer("John", "Doe", expectedID, "john@email.com", "123456789", null, LocalDate.of(2000, 1, 10), 
    "johndoe", "doe123", NotificationType.EMAIL, new ArrayList<String>(), new ArrayList<Credential>(),
     new ArrayList<String>(),true, false, 10.0);

     assertEquals(expectedID, user.getUserId());
   }

   @Test 
   public void createVolunteerAccount(){
    Volunteer example = createVolunteerExample();
    UUID expectedID = UUID.randomUUID();
     User result = User.makeAccount(example,"John","Doe",expectedID,"john@email.com","123456789",null,LocalDate.of(2000, 1, 10),true,
            25.0,new ArrayList<String>(),new ArrayList<Credential>(),new ArrayList<String>(),true,false,10.0,0,null,new ArrayList<String>()
        );

         assertNotNull(result);
        assertInstanceOf(Volunteer.class, result);
        assertEquals(expectedID, result.getUserId());
   }

   @Test 
   public void createRequesterAccount(){
    AidRequester example = createAidRequesterExample();
    UUID expectedID = UUID.randomUUID();
     User result = User.makeAccount(example,"Jane","Doe",expectedID,"jane@email.com","987654321",null,
     LocalDate.of(2000, 2, 20),false,0.0,new ArrayList<String>(),new ArrayList<Credential>(),new ArrayList<String>(),
     false,false,0.0,4,null,new ArrayList<String>()
        );

        assertNotNull(result);
        assertInstanceOf(AidRequester.class, result);
        assertEquals(expectedID, result.getUserId());


   }

   
}