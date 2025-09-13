package com.example.MB_beauty_club_backend.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum WorkerCategory {

    HAIRDRESSER("Подстригване"),
    NAIL_TECHNICIAN("Маникюр и Педикюр"),
    MAKEUP_ARTIST("Грим"),
    LASH_TECHNICIAN("Миглопластика"),
    MASSEUSE("Масаж");

    private final String description;


}
