package it.chiara.vinylshop.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class InfoMsg {

    private LocalDate data;
    private String message;

    public InfoMsg(LocalDate data, String message) {
        this.data = data;
        this.message = message;
    }
}

//classe usata per inviare messaggi di feedback al frontend o al chiamante dell'API.