package com.example.MB_beauty_club_backend.services.impl;


import com.example.MB_beauty_club_backend.models.dto.VacationDTO;
import com.example.MB_beauty_club_backend.models.entity.User;
import com.example.MB_beauty_club_backend.models.entity.Vacation;
import com.example.MB_beauty_club_backend.models.entity.Worker;
import com.example.MB_beauty_club_backend.repositories.UserRepository;
import com.example.MB_beauty_club_backend.repositories.VacationRepository;
import com.example.MB_beauty_club_backend.repositories.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacationService {

    private final VacationRepository vacationRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;

    public VacationDTO findById(Long id) {
        return vacationRepository.findById(id)
                .map(vacation -> modelMapper.map(vacation, VacationDTO.class))
                .orElse(null);
    }


    public VacationDTO save(VacationDTO vacationDTO) throws ChangeSetPersister.NotFoundException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User authenticatedUser = userRepository.findByEmail(email).orElseThrow(ChangeSetPersister.NotFoundException::new);
        Worker worker = workerRepository.findByUserAndDeletedFalse(authenticatedUser).orElseThrow(ChangeSetPersister.NotFoundException::new);

        Vacation vacation = modelMapper.map(vacationDTO, Vacation.class);
        vacation.setWorker(worker);
        Vacation savedVacation = vacationRepository.save(vacation);
        return modelMapper.map(savedVacation, VacationDTO.class);
    }


    public void deleteById(Long id) {
        vacationRepository.deleteById(id);
    }

    public List<VacationDTO> findAllByDeletedFalse() {
        return vacationRepository.findAllByDeletedFalse().stream()
                .map(vacation -> modelMapper.map(vacation, VacationDTO.class))
                .collect(Collectors.toList());
    }

    public List<VacationDTO> findByAuthenticatedWorker() throws ChangeSetPersister.NotFoundException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User authenticatedUser = userRepository.findByEmail(email).orElseThrow(ChangeSetPersister.NotFoundException::new);
        Worker worker = workerRepository.findByUserAndDeletedFalse(authenticatedUser).orElseThrow(ChangeSetPersister.NotFoundException::new);

        return vacationRepository.findByWorkerAndDeletedFalse(worker).stream()
                .map(vacation -> modelMapper.map(vacation, VacationDTO.class))
                .collect(Collectors.toList());
    }

    public List<VacationDTO> findByWorkerId(Long id) throws ChangeSetPersister.NotFoundException {

        Worker worker = workerRepository.findById(id).orElseThrow(ChangeSetPersister.NotFoundException::new);

        return vacationRepository.findByWorkerAndDeletedFalse(worker).stream()
                .map(vacation -> modelMapper.map(vacation, VacationDTO.class))
                .collect(Collectors.toList());
    }

}
