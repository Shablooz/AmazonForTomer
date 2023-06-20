package BGU.Group13B.backend.Repositories.Implementations.BasketProductRepositoryImpl;

import BGU.Group13B.backend.User.BasketProduct;
import jakarta.persistence.*;

import java.util.LinkedList;
import java.util.List;

@Entity
public class ListBasketProduct {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL,fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinTable(name = "ListBasketProduct_BasketProducts",
            joinColumns = {@JoinColumn(name = "ListBasketProduct_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "BasketProduct_id", referencedColumnName = "id")})
    private List<BasketProduct> basketProducts;

    public ListBasketProduct() {
        basketProducts =new LinkedList<>();
    }
    public ListBasketProduct(List<BasketProduct> basketProducts) {
        this.basketProducts = basketProducts;

    }
    public void clear()
    {
        basketProducts.clear();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<BasketProduct> getBasketProducts() {
        return basketProducts;
    }

    public void setBasketProducts(List<BasketProduct> basketProducts) {
        this.basketProducts = basketProducts;
    }

}
