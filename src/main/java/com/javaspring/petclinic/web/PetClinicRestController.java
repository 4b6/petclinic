package com.javaspring.petclinic.web;

import com.javaspring.petclinic.exception.InternalServerException;
import com.javaspring.petclinic.exception.OwnerNotFoundException;
import com.javaspring.petclinic.model.Owner;
import com.javaspring.petclinic.service.PetClinicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/rest")
public class PetClinicRestController {

    @Autowired
    private PetClinicService petClinicService;

    @RequestMapping(method = RequestMethod.GET,value = "/owners")
    public ResponseEntity<List<Owner>> getOwners(){
        try {
            List<Owner> owners=petClinicService.findOwners();
            return ResponseEntity.ok(owners);
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @RequestMapping(method = RequestMethod.GET,value = "/owner ")
    public ResponseEntity<List<Owner>> getOwners(@RequestParam("ln") String lastName){
        try {
           List<Owner> owners=petClinicService.findOwners(lastName);
           return ResponseEntity.ok(owners);
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @RequestMapping(method =RequestMethod.GET,value = "/owner/{id}")
    public ResponseEntity<Owner> getOwner(@PathVariable("id") Long id){
        try {
           Owner owner=petClinicService.findOwner(id);
           return ResponseEntity.ok(owner);
        }catch (OwnerNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    /* <------------------------HATEOAS EXAMPLE getOwner----------------------->*/
    @RequestMapping(method = RequestMethod.GET,value = "/owner/{id}",produces = "application/json")
    public ResponseEntity<?> getOwnerAsHateoasResource(@PathVariable("id") Long id){
        try {
            Owner owner=petClinicService.findOwner(id);

            Link self= ControllerLinkBuilder.linkTo(PetClinicRestController.class).slash("/owner/"+id).withSelfRel();
            Link create=ControllerLinkBuilder.linkTo(PetClinicRestController.class).slash("/owner/").withRel("create");
            Link update=ControllerLinkBuilder.linkTo(PetClinicRestController.class).slash("/owner/"+id).withRel("update");
            Link delete=ControllerLinkBuilder.linkTo(PetClinicRestController.class).slash("/owner/"+id).withRel("delete");

            Resource<Owner> resource=new Resource<Owner>(owner,self,create,update,delete);

            return ResponseEntity.ok(resource);
        }catch (OwnerNotFoundException ex){
            return ResponseEntity.notFound().build();
        }
    }
    /* <---------end------------HATEOAS EXAMPLE getOwner---------end----------->*/
    @RequestMapping(method = RequestMethod.POST,value = "/owner")// Insert
    public ResponseEntity<URI> createOwner(@RequestBody Owner owner){//URI owner kaydına erisim saglar . . .
        try {
            Long id =petClinicService.createOwner(owner);
            URI location= ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{id}")
                    .buildAndExpand(id).toUri();
            return ResponseEntity.created(location).build();
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @RequestMapping(method = RequestMethod.PUT,value = "/owner/{id},")//Update
    public ResponseEntity<?>updateOwner(@PathVariable("id") Long id,
                                        @RequestBody Owner ownerRequest){
        try {
            Owner owner=petClinicService.findOwner(id);
            owner.setFirstName(ownerRequest.getFirstName());
            owner.setLastName(ownerRequest.getLastName());

            petClinicService.update(owner);
            return ResponseEntity.ok().build();
        }catch (OwnerNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @RequestMapping(method = RequestMethod.DELETE,value = "/owner/{id}")
    public ResponseEntity<?> deleteOwner(@PathVariable("id") Long id){
        try {
            petClinicService.deleteOwner(id);
            return ResponseEntity.ok().build();
        }catch (OwnerNotFoundException ex){
            throw ex;
        }catch (Exception ex){
            throw new InternalServerException(ex);
        }
    }
}
