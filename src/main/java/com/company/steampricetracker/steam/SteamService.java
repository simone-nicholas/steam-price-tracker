package com.company.steampricetracker.Steam.dto;

import com.company.steampricetracker.Steam.SteamClient;
import org.springframework.stereotype.Service;

@Service
public class SteamService {
    private final SteamClient steamClient;

    public SteamService(SteamClient steamClient) {
        this.steamClient = steamClient;
    }


}
