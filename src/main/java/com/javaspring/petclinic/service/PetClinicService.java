package com.javaspring.petclinic.service;

import com.javaspring.petclinic.exception.OwnerNotFoundException;
import com.javaspring.petclinic.exception.VetNotFoundException;
import com.javaspring.petclinic.model.Owner;
import com.javaspring.petclinic.model.Vet;

import java.util.List;

public interface PetClinicService {

    List<Owner> findOwners();
    List<Owner> findOwners(String lastName);
    Owner findOwner(Long id) throws OwnerNotFoundException;

    Long createOwner(Owner owner);
    void update(Owner owner);
    void deleteOwner(Long id);

    List<Vet> findVets();
    Vet findVet(Long id) throws VetNotFoundException;




}
