package demo.mapper;

import demo.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag,Long> {

    @Query("select t from Tag t")
    List<Tag> findTop(Pageable pageable);

    // 根据名称获取Tag
    Tag findByName(String name);


}
