package com.example.MB_beauty_club_backend.services.impl;

import com.example.MB_beauty_club_backend.models.dto.WorkerDTO;
import com.example.MB_beauty_club_backend.models.dto.WorkingHoursDTO;
import com.example.MB_beauty_club_backend.models.entity.User;
import com.example.MB_beauty_club_backend.models.entity.Worker;
import com.example.MB_beauty_club_backend.models.entity.WorkingHours;
import com.example.MB_beauty_club_backend.repositories.UserRepository;
import com.example.MB_beauty_club_backend.repositories.WorkerRepository;
import com.example.MB_beauty_club_backend.repositories.WorkingHoursRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkingHoursService {

    private final WorkingHoursRepository workingHoursRepository;
    private final WorkerService workerService;
    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public List<WorkingHoursDTO> findByWorkerId() throws ChangeSetPersister.NotFoundException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User authenticatedUser = userRepository.findByEmail(email).orElseThrow(ChangeSetPersister.NotFoundException::new);

        Worker worker = workerRepository.findByUser(authenticatedUser).orElseThrow(ChangeSetPersister.NotFoundException::new);

        List<WorkingHours> workingHours = workingHoursRepository.findByWorker(worker);
        return workingHours.stream()
                .map(x -> modelMapper.map(x, WorkingHoursDTO.class))
                .toList();
    }

    public void delete(Long id) throws ChangeSetPersister.NotFoundException {
        WorkingHours workingHours = workingHoursRepository.findById(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        workingHoursRepository.delete(workingHours);
    }

    @Transactional
    public void setWorkingHoursForWorker(List<WorkingHoursDTO> newHours) throws ChangeSetPersister.NotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User authenticatedUser = userRepository.findByEmail(email).orElseThrow(ChangeSetPersister.NotFoundException::new);
        Worker worker = workerRepository.findByUser(authenticatedUser).orElseThrow(ChangeSetPersister.NotFoundException::new);

        // Delete all existing working hours for the worker
        workingHoursRepository.deleteAllByWorker(worker);

        // Use ModelMapper to convert DTOs to entities and then save them
        List<WorkingHours> hoursToSave = newHours.stream()
                .map(dto -> {
                    WorkingHours entity = modelMapper.map(dto, WorkingHours.class);
                    // Manually set the worker reference since it's not in the DTO
                    entity.setWorker(worker);
                    return entity;
                })
                .toList(); // Using the modern .toList()
        workingHoursRepository.saveAll(hoursToSave);
    }

}
