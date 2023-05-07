package BGU.Group13B.backend.storePackage;


import java.util.List;
import java.util.function.Predicate;

public interface IRepository<TEntity/*entity*/, TPrimaryKey/*primary key*/> {

    /**
     * @param from - the first index of the page in the sorted list
     * @param to - the last index of the page in the sorted list
     * @return a list of entities in the range [@param from, @param to]
     * @throws IllegalArgumentException if from > to
     *
     * */
    List<TEntity> getPage(int from, int to);

    /**
     * @param filter - a predicate that filters the entities
     * @return a list of entities that satisfy the predicate
     * @throws IllegalArgumentException if filter is null
     * @apiNote used mainly for tests
     * */
    List<TEntity> getByFilter(Predicate<TEntity> filter);

    List<TEntity> getAll();
    TEntity get(TPrimaryKey id);
    boolean contains(TPrimaryKey id);
    void add(TEntity entity);
    void remove(TPrimaryKey id);

    /*
    * consider concurrency issues
    * */
    void update(TEntity entity, TPrimaryKey id);


}
