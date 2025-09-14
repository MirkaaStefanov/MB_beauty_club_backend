package com.example.MB_beauty_club_backend.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum WorkerCategory {

    HAIRSTYLING("Подстригване"),
    NAIL("Маникюр и Педикюр"),
    MAKEUP("Грим"),
    LASH("Миглопластика"),
    MASSAGE("Масаж");

    private final String description;


}
