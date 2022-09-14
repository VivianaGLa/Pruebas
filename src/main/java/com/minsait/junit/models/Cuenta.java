package com.minsait.junit.models;
import java.math.BigDecimal;
import com.minsait.junit.exception.DineroInsuficienteException;
import lombok.*;

@Data
public class Cuenta {
    @NonNull
    private String persona;
    @NonNull
    private BigDecimal saldo;
    private Banco banco;

    public void retirar(BigDecimal monto){
        BigDecimal saldoAux=this.saldo.subtract(monto);
            if (saldoAux.compareTo(BigDecimal.ZERO) < 0) {
                throw new DineroInsuficienteException("Dinero insuficiente");
            }
        this.saldo=saldoAux;
    }
     public void depositar(BigDecimal monto){this.saldo=this.saldo.add(monto);}
}