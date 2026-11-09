package com.enchantea.application.dlv;

import java.util.ArrayList;
import java.util.List;

import com.enchantea.domain.reg.AccountType;
import com.enchantea.domain.reg.Courier;
import com.enchantea.domain.reg.CourierStatus;
import com.enchantea.domain.reg.Location;
import com.enchantea.domain.reg.User;
import com.enchantea.foundation.reg.UserRepository;

public class CourierSelector {
    private UserRepository userRepository;
    
    public List<Courier> idleCouriers(){
        List<User> allUsers = userRepository.findAll();
        if(allUsers.isEmpty()){
            throw new IllegalStateException("There are no users in the database.");
        }

        List<Courier> allIdleCouriers = new ArrayList<Courier>();

        for (User user : allUsers){
            if (user.getAccountType() == AccountType.COURIER && (user instanceof Courier)){
                Courier c = (Courier) user;
                if(c.getState().status() == CourierStatus.IDLE){
                    allIdleCouriers.add(c);
                }
            }
        }

        return allIdleCouriers;
    }

    public Courier select(Location restaurantLocation){
        List<Courier> allIdleCouriers = new ArrayList<Courier>();
        try{
            allIdleCouriers = idleCouriers();
        } catch (IllegalStateException e){
            System.out.println(e);
        }

        if(allIdleCouriers.isEmpty()){
            return null;
        }

        Courier candidate = allIdleCouriers.get(0);

        if(allIdleCouriers.size()==1){
            return candidate;
        }

        double x1=restaurantLocation.getLat();
        double y1=restaurantLocation.getLng();
        double currentx=candidate.getPosition().getLat();
        double currenty=candidate.getPosition().getLng();

        double currentDistance = Math.sqrt(Math.pow(currentx-x1, 2) + Math.pow(currenty-y1, 2));

        for (int i=0; i<allIdleCouriers.size(); i++){
            double x2=allIdleCouriers.get(i).getPosition().getLat();
            double y2=allIdleCouriers.get(i).getPosition().getLng();

            double distance = Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));

            if(distance<currentDistance){
                candidate=allIdleCouriers.get(i);
                currentDistance=distance;
            }else if (distance==currentDistance){
                if(candidate.getUserId()>allIdleCouriers.get(i).getUserId()){
                    candidate=allIdleCouriers.get(i);
                    currentDistance=distance;
                }
            }
        }
        return candidate;
    }
}
