package com.example.MB_beauty_club_backend.services.impl;

import com.example.MB_beauty_club_backend.enums.WorkerCategory;
import com.example.MB_beauty_club_backend.models.dto.ServiceDTO;
import com.example.MB_beauty_club_backend.models.entity.Product;
import com.example.MB_beauty_club_backend.repositories.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceService {


    private final ServiceRepository serviceRepository;
    private final ModelMapper modelMapper;


    public ServiceDTO save(ServiceDTO serviceDTO) {
        com.example.MB_beauty_club_backend.models.entity.Service service = modelMapper.map(serviceDTO, com.example.MB_beauty_club_backend.models.entity.Service.class);
        com.example.MB_beauty_club_backend.models.entity.Service savedService = serviceRepository.save(service);
        return modelMapper.map(savedService, ServiceDTO.class);
    }

    public ServiceDTO findById(UUID id) {
        return serviceRepository.findById(id)
                .map(service -> modelMapper.map(service, ServiceDTO.class))
                .orElse(null);
    }

    public void deleteById(UUID id) throws ChangeSetPersister.NotFoundException {
        com.example.MB_beauty_club_backend.models.entity.Service service = serviceRepository.findById(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        service.setDeleted(true);
        serviceRepository.save(service);
    }

    public List<ServiceDTO> findAll() {
        return serviceRepository.findAllByDeletedFalse().stream()
                .map(service -> modelMapper.map(service, ServiceDTO.class))
                .collect(Collectors.toList());
    }

    public List<ServiceDTO> findByCategory(WorkerCategory category) {
        return serviceRepository.findByCategoryAndDeletedFalse(category).stream()
                .map(service -> modelMapper.map(service, ServiceDTO.class))
                .collect(Collectors.toList());
    }

}
