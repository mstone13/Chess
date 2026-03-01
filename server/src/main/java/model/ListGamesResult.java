package model;

public record ListGamesResult (
     int gameID,
     String whiteUsername,
     String blackUsername,
     String gameName
     ){}