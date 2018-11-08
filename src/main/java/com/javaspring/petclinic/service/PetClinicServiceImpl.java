package com.javaspring.petclinic.service;

import com.javaspring.petclinic.dao.OwnerRepository;
import com.javaspring.petclinic.dao.PetRepository;
import com.javaspring.petclinic.dao.VetRepository;
import com.javaspring.petclinic.exception.OwnerNotFoundException;
import com.javaspring.petclinic.exception.VetNotFoundException;
import com.javaspring.petclinic.model.Owner;
import com.javaspring.petclinic.model.Vet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)//check exception gordugunde de rollback yap
public class PetClinicServiceImpl implements PetClinicService{

    private OwnerRepository ownerRepository;

    private PetRepository petRepository;

    private VetRepository vetRepository;

    @Autowired
    public void setVetRepository(VetRepository vetRepository) {
        this.vetRepository = vetRepository;
    }

    @Autowired
    private JavaMailSender javaMailSender;

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
        SimpleMailMessage msg=new SimpleMailMessage();
        msg.setFrom("ab@g");
        msg.setTo("ba@g");
        msg.setSubject("Owner created !");
        msg.setText("Owner entity with id : "+owner.getId()+"created successfully !");
        javaMailSender.send(msg);

        /*zengin içerikli mail gönderimi;
        MimeMessage message=javaMailSender.createMimeMessage();
        MimeMessageHelper helper=new MimeMessageHelper(message,true);
        helper.setFrom("me@example.com");
        helper.setTo("test@host.com");
        helper.setText("<html><body>Thank you!</body></html>",true);

        FileSystemResource fileSystemResource=new FileSystemResource(new File("c://profile.jpg"));

        helper.addAttachment("profile.jpg",fileSystemResource);
        javaMailSender.send(message);*/
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

    @Override
    public List<Vet> findVets() {
        return vetRepository.findAll();
    }

    @Override
    public Vet findVet(Long id) throws VetNotFoundException {
        vetRepository.findById(id).orElseThrow(()->{return new VetNotFoundException("Vet not found by id : "+id);});
        return null;
    }
}
