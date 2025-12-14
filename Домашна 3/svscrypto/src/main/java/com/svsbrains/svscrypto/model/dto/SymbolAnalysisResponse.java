package com.svsbrains.svscrypto.model.dto;

import java.util.List;
import java.util.Map;

public class SymbolAnalysisResponse {

    private String symbol;
    private List<String> _1D;
    private List<String> _1W;
    private List<String> _1M;

    // Jackson mapping
    public List<String> get1D() { return _1D; }
    public void set1D(List<String> v) { this._1D = v; }

    public List<String> get1W() { return _1W; }
    public void set1W(List<String> v) { this._1W = v; }

    public List<String> get1M() { return _1M; }
    public void set1M(List<String> v) { this._1M = v; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
}
