package com.klaxon.kserver.controller.api;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.*;

import com.klaxon.kserver.bean.Response;
import com.klaxon.kserver.controller.vo.WebPageTaskVo;
import com.klaxon.kserver.converter.WebPageTaskMapperStruct;
import com.klaxon.kserver.handler.ImageResourceHttpRequestHandler;
import com.klaxon.kserver.mapper.WebPageTaskMapper;
import com.klaxon.kserver.mapper.model.WebPageTask;
import com.klaxon.kserver.property.YtDlpProperty;
import com.klaxon.kserver.service.WebPageTaskService;
import com.klaxon.kserver.service.dto.WebPageTaskDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/web-page-task")
public class WebPageTaskController {

	@Resource
	private YtDlpProperty ytDlpProperty;
	@Resource
	private WebPageTaskMapper webPageTaskMapper;
	@Resource
	private WebPageTaskService webPageTaskService;
	@Resource
	private WebPageTaskMapperStruct webPageTaskMapperStruct;
	@Resource
	private ImageResourceHttpRequestHandler imageResourceHttpRequestHandler;

	@PostMapping("/list")
	public Response<Object> list() {
		return Response.success(webPageTaskService.list(new WebPageTaskDto()));
	}

	@PostMapping("/retry")
	public Response<Object> retry(@RequestBody WebPageTaskVo webPageTaskVo) {
		WebPageTaskDto webPageTaskDTO = webPageTaskMapperStruct.voToDto(webPageTaskVo);
		webPageTaskService.retry(webPageTaskDTO);
		return Response.success();
	}

	@PostMapping("/pause")
	public Response<Object> pause(@RequestBody WebPageTaskVo webPageTaskVo) {
		WebPageTaskDto webPageTaskDTO = webPageTaskMapperStruct.voToDto(webPageTaskVo);
		webPageTaskService.pause(webPageTaskDTO);
		return Response.success();
	}

	@PostMapping("/remove")
	public Response<Object> remove(@RequestBody WebPageTaskVo webPageTaskVo) {
		WebPageTaskDto webPageTaskDTO = webPageTaskMapperStruct.voToDto(webPageTaskVo);
		webPageTaskService.remove(webPageTaskDTO);
		return Response.success();
	}

	@GetMapping("/img/{taskId}")
	public void getImage(@PathVariable Long taskId, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws IOException {

		WebPageTask task = webPageTaskMapper.selectById(taskId);
		String path = ytDlpProperty.getDestination() + task.getThumbnailPath();
		path = path.replace("\\", "/");

		log.info("图片路径: {}", path);
		Path thumbnailPath = Paths.get(path);
		httpServletRequest.setAttribute(ImageResourceHttpRequestHandler.ATTRIBUTE_FILE, thumbnailPath.toFile());
		try {
			imageResourceHttpRequestHandler.handleRequest(httpServletRequest, httpServletResponse);
		} catch (ServletException ignored) {
		}
	}

	@GetMapping("/video/{taskId}")
	public void getVideo(@PathVariable Long taskId, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws IOException {

		WebPageTask task = webPageTaskMapper.selectById(taskId);
		String path = ytDlpProperty.getDestination() + task.getFilePath();
		path = path.replace("\\", "/");

		log.info("视频路径: {}", path);
		Path videoPath = Paths.get(path);
		httpServletRequest.setAttribute(ImageResourceHttpRequestHandler.ATTRIBUTE_FILE, videoPath.toFile());
		try {
			imageResourceHttpRequestHandler.handleRequest(httpServletRequest, httpServletResponse);
		} catch (ServletException ignored) {
		}
	}

}
