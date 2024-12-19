package com.app.meurole;

import java.util.Date;

public class Usuario {
    private String nome;
    private String CPF;
    private Date dob;
    private String email;

    public Usuario() {}

    public Usuario(String nome, String CPF, Date dob, String email) {
        this.nome = nome;
        this.CPF = CPF;
        this.dob = dob;
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCPF() {
        return CPF;
    }

    public void setCPF(String CPF) {
        this.CPF = CPF;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
