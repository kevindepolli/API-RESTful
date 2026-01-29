package model;

import java.util.List;

// Records são imutáveis e menos verbosos
public record PagedResult<T>(List<T> data, long total, int page, int pageSize) {}