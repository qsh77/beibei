package com.beibeijava.mapper;

import com.beibeijava.entity.Worker;
import com.beibeijava.vo.WorkerDetailVO;
import com.beibeijava.vo.WorkerListVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 阿姨数据访问层
 */
@Mapper
public interface WorkerMapper {

        /**
         * 根据ID查询阿姨
         */
        @Select("SELECT * FROM worker WHERE id = #{id}")
        Worker findById(@Param("id") Long id);

        /**
         * 根据用户ID查询阿姨
         */
        @Select("SELECT * FROM worker WHERE user_id = #{userId}")
        Worker findByUserId(@Param("userId") Long userId);

        /**
         * 插入阿姨记录
         */
        @Insert("INSERT INTO worker (user_id, level, years, bio, score, status, created_at, updated_at) " +
                        "VALUES (#{userId}, #{level}, #{years}, #{bio}, #{score}, #{status}, NOW(), NOW())")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(Worker worker);

        /**
         * 更新阿姨信息
         */
        @Update("UPDATE worker SET level = #{level}, years = #{years}, bio = #{bio}, " +
                        "score = #{score}, status = #{status}, updated_at = NOW() WHERE id = #{id}")
        int update(Worker worker);

        /**
         * 更新阿姨状态
         */
        @Update("UPDATE worker SET status = #{status} WHERE id = #{id}")
        int updateStatus(@Param("id") Long id, @Param("status") Integer status);

        /**
         * 更新阿姨评分
         */
        @Update("UPDATE worker SET score = #{score} WHERE id = #{id}")
        int updateScore(@Param("id") Long id, @Param("score") java.math.BigDecimal score);

        /**
         * 同时更新阿姨评分和等级
         */
        @Update("UPDATE worker SET score = #{score}, level = #{level} WHERE id = #{id}")
        int updateScoreAndLevel(@Param("id") Long id, @Param("score") java.math.BigDecimal score,
                        @Param("level") Integer level);

        // ===== 管理员阿姨管理方法 =====

        /**
         * 分页查询阿姨列表
         */
        @Select("<script>" +
                        "SELECT " +
                        "w.id, w.user_id, w.level, w.years, w.bio, w.score, w.status, w.created_at, " +
                        "u.phone, " +
                        "up.name, up.gender, up.avatar, " +
                        "COUNT(o.id) as total_orders, " +
                        "COUNT(CASE WHEN o.status = 'DONE' THEN 1 END) as completed_orders " +
                        "FROM worker w " +
                        "JOIN user u ON w.user_id = u.id " +
                        "LEFT JOIN user_profile up ON u.id = up.user_id " +
                        "LEFT JOIN `order` o ON w.id = o.worker_id " +
                        "WHERE 1=1 " +
                        "<if test='keyword != null and keyword != \"\"'>" +
                        "AND (u.phone LIKE CONCAT('%', #{keyword}, '%') OR up.name LIKE CONCAT('%', #{keyword}, '%')) "
                        +
                        "</if>" +
                        "<if test='level != null'>" +
                        "AND w.level = #{level} " +
                        "</if>" +
                        "<if test='status != null'>" +
                        "AND w.status = #{status} " +
                        "</if>" +
                        "<if test='minScore != null'>" +
                        "AND w.score &gt;= #{minScore} " +
                        "</if>" +
                        "<if test='maxScore != null'>" +
                        "AND w.score &lt;= #{maxScore} " +
                        "</if>" +
                        "GROUP BY w.id, w.user_id, w.level, w.years, w.bio, w.score, w.status, " +
                        "u.phone, u.created_at, up.name, up.gender, up.avatar " +
                        "<choose>" +
                        "<when test='sortBy == \"scoreDesc\"'>ORDER BY w.score DESC, w.created_at DESC </when>" +
                        "<when test='sortBy == \"scoreAsc\"'>ORDER BY w.score ASC, w.created_at DESC </when>" +
                        "<otherwise>ORDER BY w.created_at DESC </otherwise>" +
                        "</choose>" +
                        "LIMIT #{offset}, #{size}" +
                        "</script>")
        List<WorkerListVO> findWorkerList(@Param("keyword") String keyword,
                        @Param("level") Integer level,
                        @Param("status") Integer status,
                        @Param("minScore") Double minScore,
                        @Param("maxScore") Double maxScore,
                        @Param("sortBy") String sortBy,
                        @Param("offset") Integer offset,
                        @Param("size") Integer size);

        /**
         * 查询阿姨总数
         */
        @Select("<script>" +
                        "SELECT COUNT(DISTINCT w.id) " +
                        "FROM worker w " +
                        "JOIN user u ON w.user_id = u.id " +
                        "LEFT JOIN user_profile up ON u.id = up.user_id " +
                        "WHERE 1=1 " +
                        "<if test='keyword != null and keyword != \"\"'>" +
                        "AND (u.phone LIKE CONCAT('%', #{keyword}, '%') OR up.name LIKE CONCAT('%', #{keyword}, '%')) "
                        +
                        "</if>" +
                        "<if test='level != null'>" +
                        "AND w.level = #{level} " +
                        "</if>" +
                        "<if test='status != null'>" +
                        "AND w.status = #{status} " +
                        "</if>" +
                        "<if test='minScore != null'>" +
                        "AND w.score >= #{minScore} " +
                        "</if>" +
                        "<if test='maxScore != null'>" +
                        "AND w.score &lt;= #{maxScore} " +
                        "</if>" +
                        "</script>")
        Long countWorkers(@Param("keyword") String keyword,
                        @Param("level") Integer level,
                        @Param("status") Integer status,
                        @Param("minScore") Double minScore,
                        @Param("maxScore") Double maxScore);

        /**
         * 查询阿姨详情
         */
        @Select("SELECT " +
                        "w.id, w.user_id, w.level, w.years, w.bio, w.score, w.status, w.created_at, w.updated_at, " +
                        "u.phone, u.role, " +
                        "up.name, up.gender, up.birthday, up.avatar, up.email, up.address, " +
                        "COUNT(o.id) as total_orders, " +
                        "COUNT(CASE WHEN o.status = 'DONE' THEN 1 END) as completed_orders, " +
                        "AVG(r.rating) as avg_rating, " +
                        "COUNT(r.id) as total_reviews " +
                        "FROM worker w " +
                        "JOIN user u ON w.user_id = u.id " +
                        "LEFT JOIN user_profile up ON u.id = up.user_id " +
                        "LEFT JOIN `order` o ON w.id = o.worker_id " +
                        "LEFT JOIN review r ON o.id = r.order_id " +
                        "WHERE w.id = #{workerId} " +
                        "GROUP BY w.id, w.user_id, w.level, w.years, w.bio, w.score, w.status, " +
                        "u.phone, u.role, u.created_at, u.updated_at, " +
                        "up.name, up.gender, up.birthday, up.avatar, up.email, up.address")
        WorkerDetailVO findWorkerDetail(@Param("workerId") Long workerId);

        /**
         * 批量更新阿姨状态
         */
        @Update("<script>" +
                        "UPDATE worker SET status = #{status} WHERE id IN " +
                        "<foreach collection='workerIds' item='id' open='(' separator=',' close=')'>" +
                        "#{id}" +
                        "</foreach>" +
                        "</script>")
        int batchUpdateStatus(@Param("workerIds") List<Long> workerIds, @Param("status") Integer status);

        /**
         * 获取阿姨统计信息 - 返回Map便于处理
         */
        @Select("SELECT " +
                        "COUNT(*) as total_workers, " +
                        "COUNT(CASE WHEN status = 1 THEN 1 END) as active_workers, " +
                        "COUNT(CASE WHEN status = 0 THEN 1 END) as inactive_workers, " +
                        "COUNT(CASE WHEN score < 2 THEN 1 END) as level1_count, " +
                        "COUNT(CASE WHEN score >= 2 AND score < 3 THEN 1 END) as level2_count, " +
                        "COUNT(CASE WHEN score >= 3 AND score < 4 THEN 1 END) as level3_count, " +
                        "COUNT(CASE WHEN score >= 4 AND score < 4.8 THEN 1 END) as level4_count, " +
                        "COUNT(CASE WHEN score >= 4.8 THEN 1 END) as level5_count, " +
                        "AVG(score) as avg_score, " +
                        "MAX(score) as max_score, " +
                        "MIN(score) as min_score " +
                        "FROM worker")
        java.util.Map<String, Object> getWorkerStatsMap();
}