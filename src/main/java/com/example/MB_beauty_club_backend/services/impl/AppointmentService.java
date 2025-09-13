package com.example.MB_beauty_club_backend.services.impl;

import com.example.MB_beauty_club_backend.enums.AppointmentStatus;
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
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public AppointmentDTO save(AppointmentDTO appointmentDTO) throws ChangeSetPersister.NotFoundException {
        // Find the related entities
        Worker worker = workerRepository.findById(appointmentDTO.getWorker().getId())
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());
        User user = userRepository.findById(appointmentDTO.getUser().getId())
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());
        com.example.MB_beauty_club_backend.models.entity.Service service = serviceRepository.findById(appointmentDTO.getService().getId())
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        // Map DTO to entity and set relationships
        Appointment appointment = modelMapper.map(appointmentDTO, Appointment.class);
        appointment.setWorker(worker);
        appointment.setUser(user);
        appointment.setService(service);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return modelMapper.map(savedAppointment, AppointmentDTO.class);
    }

    public void deleteById(Long id) {
        appointmentRepository.deleteById(id);
    }

    public List<AppointmentDTO> findByWorker(Long id) throws ChangeSetPersister.NotFoundException {

        Worker worker = workerRepository.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        return appointmentRepository.findByWorker(worker).stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> findByUser() throws ChangeSetPersister.NotFoundException {
        User authenticatedUser = getAuthenticatedUser();

        return appointmentRepository.findByUser(authenticatedUser).stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> findPendingByWorker() throws ChangeSetPersister.NotFoundException {
        User authenticatedUser = getAuthenticatedUser();
        Worker worker = workerRepository.findByUser(authenticatedUser)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        return appointmentRepository.findByWorkerAndStatus(worker, AppointmentStatus.PENDING).stream()
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
