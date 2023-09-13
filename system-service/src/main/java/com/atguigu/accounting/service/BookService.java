package com.atguigu.accounting.service;

import com.atguigu.accounting.entity.Book;
import com.atguigu.accounting.entity.vo.SearchVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Atguigu
 * @since 2023-07-28
 */
public interface BookService extends IService<Book> {

    void importBook(MultipartFile file, Long userId);

    Page getPageList(Integer pageNum, Integer pageSize, SearchVo searchVo, Long id);

    void exportAccount(HttpServletResponse response, Long userId);

    Map<String, List<String>> getMonthlyCost(String start, String end, Long userId);

    Map<String, List<String>> getLargeAreaData(Long userId);
}
