package com.example.MB_beauty_club_backend.services.impl;

import com.example.MB_beauty_club_backend.enums.AppointmentStatus;
import com.example.MB_beauty_club_backend.enums.Role;
import com.example.MB_beauty_club_backend.models.dto.AppointmentDTO;
import com.example.MB_beauty_club_backend.models.entity.Appointment;
import com.example.MB_beauty_club_backend.models.entity.User;
import com.example.MB_beauty_club_backend.models.entity.Worker;
import com.example.MB_beauty_club_backend.repositories.AppointmentRepository;
import com.example.MB_beauty_club_backend.repositories.ServiceRepository;
import com.example.MB_beauty_club_backend.repositories.UserRepository;
import com.example.MB_beauty_club_backend.repositories.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.ValidationException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final ModelMapper modelMapper;

    private User getAuthenticatedUser() throws ChangeSetPersister.NotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
    }

    public AppointmentDTO findById(Long id) {
        return appointmentRepository.findById(id)
                .map(appointment -> modelMapper.map(appointment, AppointmentDTO.class))
                .orElse(null);
    }

    public AppointmentDTO save(AppointmentDTO appointmentDTO) throws Exception {

        User user = getAuthenticatedUser();
        Worker worker = workerRepository.findById(appointmentDTO.getWorker().getId())
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());
        com.example.MB_beauty_club_backend.models.entity.Service service = serviceRepository.findById(appointmentDTO.getService().getId())
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());


        if (appointmentDTO.getStartTime().isBefore(LocalDate.now().atStartOfDay())) {
            throw new Exception();
        }

        Appointment appointment = modelMapper.map(appointmentDTO, Appointment.class);
        appointment.setWorker(worker);
        if (user.getRole().equals(Role.WORKER)) {
            appointment.setStatus(AppointmentStatus.CONFIRMED);
        }
        if (appointment.getUsername() == null) {
            appointment.setUser(user);
        }
        appointment.setService(service);
        appointment.setEndTime(appointment.getStartTime().plusMinutes(service.getDuration()));

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return modelMapper.map(savedAppointment, AppointmentDTO.class);
    }

    public void deleteById(Long id) throws ChangeSetPersister.NotFoundException {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        appointment.setDeleted(true);
        appointmentRepository.save(appointment);
    }

    public List<AppointmentDTO> findByWorker(UUID id) throws ChangeSetPersister.NotFoundException {

        Worker worker = workerRepository.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        return appointmentRepository.findByWorkerAndDeletedFalse(worker).stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> findByAuthenticated() throws ChangeSetPersister.NotFoundException {
        User authenticatedUser = getAuthenticatedUser();

        if (authenticatedUser.getRole().equals(Role.WORKER)) {

            Worker worker = workerRepository.findByUserAndDeletedFalse(authenticatedUser).orElseThrow(ChangeSetPersister.NotFoundException::new);

            List<Appointment> appointments = appointmentRepository.findByWorkerAndDeletedFalse(worker);
            return appointmentRepository.findByWorkerAndDeletedFalse(worker).stream()
                    .map(appointment -> modelMapper.map(appointment, AppointmentDTO.class))
                    .collect(Collectors.toList());
        }

        if (authenticatedUser.getRole().equals(Role.USER)) {
            return appointmentRepository.findByUserAndDeletedFalse(authenticatedUser).stream()
                    .map(appointment -> modelMapper.map(appointment, AppointmentDTO.class))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    public List<AppointmentDTO> findPendingByWorker() throws ChangeSetPersister.NotFoundException {
        User authenticatedUser = getAuthenticatedUser();
        Worker worker = workerRepository.findByUserAndDeletedFalse(authenticatedUser)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        return appointmentRepository.findByWorkerAndStatusAndDeletedFalse(worker, AppointmentStatus.PENDING).stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    public AppointmentDTO updateStatus(Long id, AppointmentStatus status) throws ChangeSetPersister.NotFoundException {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        appointment.setStatus(status);
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return modelMapper.map(updatedAppointment, AppointmentDTO.class);
    }
}
