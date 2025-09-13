package com.example.MB_beauty_club_backend.services.impl;

import com.example.MB_beauty_club_backend.enums.Role;
import com.example.MB_beauty_club_backend.enums.WorkerCategory;
import com.example.MB_beauty_club_backend.models.dto.WorkerDTO;
import com.example.MB_beauty_club_backend.models.entity.User;
import com.example.MB_beauty_club_backend.models.entity.Worker;
import com.example.MB_beauty_club_backend.repositories.UserRepository;
import com.example.MB_beauty_club_backend.repositories.WorkerRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public WorkerDTO createWorker(UUID userId, WorkerCategory workerCategory) throws ChangeSetPersister.NotFoundException {

        User user = userRepository.findById(userId).orElseThrow(ChangeSetPersister.NotFoundException::new);

        Worker optional = workerRepository.findByUser(user).orElseThrow(ValidationException::new);

        Worker worker = new Worker();
        worker.setWorkerCategory(workerCategory);
        worker.setUser(user);
        worker.setName(user.getName());
        user.setRole(Role.WORKER);
        userRepository.save(user);
        return modelMapper.map(workerRepository.save(worker), WorkerDTO.class);
    }

}
