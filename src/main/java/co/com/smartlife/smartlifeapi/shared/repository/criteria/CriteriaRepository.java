package co.com.smartlife.smartlifeapi.shared.repository.criteria;

import java.util.List;

public interface CriteriaRepository<E> { List<E> matching(Criteria criteria); }
