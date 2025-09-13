package com.example.MB_beauty_club_backend.services.impl;

import com.example.MB_beauty_club_backend.enums.WorkerCategory;
import com.example.MB_beauty_club_backend.models.dto.ServiceDTO;
import com.example.MB_beauty_club_backend.repositories.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public ServiceDTO findById(Long id) {
        return serviceRepository.findById(id)
                .map(service -> modelMapper.map(service, ServiceDTO.class))
                .orElse(null);
    }

    public void deleteById(Long id) {
        serviceRepository.deleteById(id);
    }

    public List<ServiceDTO> findAll() {
        return serviceRepository.findAll().stream()
                .map(service -> modelMapper.map(service, ServiceDTO.class))
                .collect(Collectors.toList());
    }

    public List<ServiceDTO> findByCategory(WorkerCategory category) {
        return serviceRepository.findByCategory(category).stream()
                .map(service -> modelMapper.map(service, ServiceDTO.class))
                .collect(Collectors.toList());
    }

}
