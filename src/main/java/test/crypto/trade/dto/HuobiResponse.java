package test.crypto.trade.dto;

import lombok.Data;

import java.util.List;

@Data
public class HuobiResponse {
    private List<HuobiTicker> data;
}