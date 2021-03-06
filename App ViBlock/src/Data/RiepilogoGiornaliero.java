package Data;

import java.math.BigDecimal;


public class RiepilogoGiornaliero {
    String data;
    BigDecimal saldoFinale;
    Integer tesseramenti;
    BigDecimal soldiTesseramenti;
    BigDecimal entrateGiornata;
    BigDecimal prelievi;

    public RiepilogoGiornaliero(){}

    public RiepilogoGiornaliero(String data, BigDecimal saldoFinale, Integer tesseramenti, BigDecimal soldiTesseramenti, BigDecimal entrateGiornata, BigDecimal prelievi) {
        this.data = data;
        this.saldoFinale = saldoFinale;
        this.tesseramenti = tesseramenti;
        this.soldiTesseramenti = soldiTesseramenti;
        this.entrateGiornata = entrateGiornata;
        this.prelievi = prelievi;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public BigDecimal getSaldoFinale() {
        return saldoFinale;
    }

    public void setSaldoFinale(BigDecimal saldoFinale) {
        this.saldoFinale = saldoFinale;
    }

    public Integer getTesseramenti() {
        return tesseramenti;
    }

    public void setTesseramenti(Integer tesseramenti) {
        this.tesseramenti = tesseramenti;
    }

    public BigDecimal getSoldiTesseramenti() {
        return soldiTesseramenti;
    }

    public void setSoldiTesseramenti(BigDecimal soldiTesseramenti) {
        this.soldiTesseramenti = soldiTesseramenti;
    }

    public BigDecimal getEntrateGiornata() {
        return entrateGiornata;
    }

    public void setEntrateGiornata(BigDecimal entrateGiornata) {
        this.entrateGiornata = entrateGiornata;
    }

    public BigDecimal getPrelievi() {
        return prelievi;
    }

    public void setPrelievi(BigDecimal prelievi) {
        this.prelievi = prelievi;
    }


}
