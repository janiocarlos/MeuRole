package com.app.meurole.model;

import java.util.Date;

public class Event {
    private String eventId;
    private String nome;
    private Long dataInMillis;
    private String local;
    private String tipo;
    private double valorInscricao;
    private String thumbUrl;
    private String creatorUid;

    // Construtor vazio exigido pelo Firebase
    public Event() {
    }

    public Event(String eventId, String nome, Date data, String local, String tipo, double valorInscricao, String thumbUrl, String creatorUid) {
        this.eventId = eventId;
        this.nome = nome;
        setData(data);
        this.local = local;
        this.tipo = tipo;
        this.valorInscricao = valorInscricao;
        this.thumbUrl = thumbUrl;
        this.creatorUid = creatorUid;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getData() {
        if (dataInMillis == null) {
            return null;
        }
        return new Date(dataInMillis);
    }

    public void setData(Date data) {
        if (data != null) {
            this.dataInMillis = data.getTime(); // Armazena em milissegundos
        } else {
            this.dataInMillis = null;
        }
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public double getValorInscricao() {
        return valorInscricao;
    }

    public void setValorInscricao(double valorInscricao) {
        this.valorInscricao = valorInscricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getCreatorUid() {
        return creatorUid;
    }

    public void setCreatorUid(String creatorUid) {
        this.creatorUid = creatorUid;
    }
}
