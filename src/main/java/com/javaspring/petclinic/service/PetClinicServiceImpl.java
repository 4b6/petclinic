package com.javaspring.petclinic.service;

import com.javaspring.petclinic.dao.OwnerRepository;
import com.javaspring.petclinic.dao.PetRepository;
import com.javaspring.petclinic.exception.OwnerNotFoundException;
import com.javaspring.petclinic.model.Owner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)//check exception gordugunde de rollback yap
public class PetClinicServiceImpl implements PetClinicService{

    private OwnerRepository ownerRepository;

    private PetRepository petRepository;

    @Autowired
    public void setOwnerRepository(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Autowired
    public void setPetRepository(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    @Secured(value = {"ROLE_USER","ROLE_EDITOR"})
    public List<Owner> findOwners() {
        return ownerRepository.findAll();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public List<Owner> findOwners(String lastName) {
        return ownerRepository.findByLastName(lastName);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public Owner findOwner(Long id) throws OwnerNotFoundException {
        Owner owner=ownerRepository.findById(id);
        if (owner==null)throw new OwnerNotFoundException("Owner not found with id : "+id);
        return owner;
    }

    @Override
    public Long createOwner(Owner owner) {
        ownerRepository.create(owner);
        return null;
    }

    @Override
    public void update(Owner owner) {
        ownerRepository.update(owner);
    }

    @Override
    public void deleteOwner(Long id) {
        petRepository.deleteByOwnerId(id);
        ownerRepository.delete(id);
        //if (true)throw new RuntimeException("testing rollback");
    }
}
