package com.example.MB_beauty_club_backend.services.impl;

import com.example.MB_beauty_club_backend.enums.Role;
import com.example.MB_beauty_club_backend.enums.WorkerCategory;
import com.example.MB_beauty_club_backend.models.dto.WorkerDTO;
import com.example.MB_beauty_club_backend.models.dto.auth.PublicUserDTO;
import com.example.MB_beauty_club_backend.models.entity.User;
import com.example.MB_beauty_club_backend.models.entity.Worker;
import com.example.MB_beauty_club_backend.repositories.UserRepository;
import com.example.MB_beauty_club_backend.repositories.WorkerRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
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

        Optional<Worker> optional = workerRepository.findByUser(user);
        if (optional.isPresent()) {
            throw new ValidationException();
        }

        Worker worker = new Worker();
        worker.setWorkerCategory(workerCategory);
        worker.setUser(user);
        worker.setName(user.getName());
        worker.setEmail(user.getEmail());
        user.setRole(Role.WORKER);
        userRepository.save(user);
        return modelMapper.map(workerRepository.save(worker), WorkerDTO.class);
    }

    public List<WorkerDTO> allWorkers() {
        List<Worker> all = workerRepository.findAll();
        return all.stream()
                .map(x -> modelMapper.map(x, WorkerDTO.class))
                .toList();
    }

    public WorkerDTO update(Long id, WorkerDTO update) {
        Worker worker = modelMapper.map(update, Worker.class);
        worker.setId(id);
        return modelMapper.map(workerRepository.save(worker), WorkerDTO.class);
    }

    public void delete(Long id) throws ChangeSetPersister.NotFoundException {
        Worker worker = workerRepository.findById(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        User user = worker.getUser();
        user.setRole(Role.USER);
        worker.setUser(null);
        userRepository.save(user);
        workerRepository.delete(worker);
    }

    public WorkerDTO findByid(Long id) throws ChangeSetPersister.NotFoundException {
        Worker worker = workerRepository.findById(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return modelMapper.map(worker, WorkerDTO.class);
    }

    public List<WorkerDTO> findByCategory(WorkerCategory workerCategory) throws ChangeSetPersister.NotFoundException {
        List<Worker> workers = workerRepository.findByWorkerCategory(workerCategory);

        return workers.stream()
                .map(x -> modelMapper.map(x, WorkerDTO.class))
                .toList();
    }


}
