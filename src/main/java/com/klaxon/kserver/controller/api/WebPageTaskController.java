package com.klaxon.kserver.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.klaxon.kserver.bean.Response;
import com.klaxon.kserver.constants.WebPageTaskConstants;
import com.klaxon.kserver.controller.vo.WebPageTaskVo;
import com.klaxon.kserver.converter.WebPageTaskMapperStruct;
import com.klaxon.kserver.exception.BizCodeEnum;
import com.klaxon.kserver.exception.BizException;
import com.klaxon.kserver.handler.ImageResourceHttpRequestHandler;
import com.klaxon.kserver.mapper.WebPageTaskMapper;
import com.klaxon.kserver.mapper.WebPageVideoTaskMapper;
import com.klaxon.kserver.mapper.model.WebPageTask;
import com.klaxon.kserver.mapper.model.WebPageVideoTask;
import com.klaxon.kserver.property.YtDlpProperty;
import com.klaxon.kserver.service.WebPageTaskService;
import com.klaxon.kserver.service.dto.WebPageTaskDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;


@Controller
@RequestMapping("/web-page-task")
public class WebPageTaskController {

	private final Logger log = LoggerFactory.getLogger(WebPageTaskController.class);

	@Resource
	private YtDlpProperty ytDlpProperty;
	@Resource
	private WebPageTaskMapper webPageTaskMapper;
	@Resource
	private WebPageVideoTaskMapper webPageVideoTaskMapper;
	@Resource
	private WebPageTaskService webPageTaskService;
	@Resource
	private WebPageTaskMapperStruct webPageTaskMapperStruct;
	@Resource
	private ImageResourceHttpRequestHandler imageResourceHttpRequestHandler;

	@PostMapping("/list")
	@ResponseBody
	public Response<Object> list() {
		return Response.success(webPageTaskService.list(new WebPageTaskDto()));
	}

	@PostMapping("/retry")
	@ResponseBody
	public Response<Object> retry(@RequestBody WebPageTaskVo webPageTaskVo) {
		WebPageTaskDto webPageTaskDTO = webPageTaskMapperStruct.voToDto(webPageTaskVo);
		webPageTaskService.retry(webPageTaskDTO);
		return Response.success();
	}

	@PostMapping("/pause")
	@ResponseBody
	public Response<Object> pause(@RequestBody WebPageTaskVo webPageTaskVo) {
		WebPageTaskDto webPageTaskDTO = webPageTaskMapperStruct.voToDto(webPageTaskVo);
		webPageTaskService.pause(webPageTaskDTO);
		return Response.success();
	}

	@PostMapping("/remove")
	@ResponseBody
	public Response<Object> remove(@RequestBody WebPageTaskVo webPageTaskVo) {
		WebPageTaskDto webPageTaskDTO = webPageTaskMapperStruct.voToDto(webPageTaskVo);
		webPageTaskService.remove(webPageTaskDTO);
		return Response.success();
	}

	@GetMapping("/img/{taskId}")
	public void getImage(@PathVariable Long taskId, HttpServletRequest httpServletRequest,
						 HttpServletResponse httpServletResponse) throws IOException {


		LambdaQueryWrapper<WebPageVideoTask> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(WebPageVideoTask::getTaskId, taskId)
				.eq(WebPageVideoTask::getVideoIndex, 1);
		WebPageVideoTask webPageVideoTask = webPageVideoTaskMapper.selectOne(queryWrapper);
		if (Objects.isNull(webPageVideoTask)) {
			throw new BizException(BizCodeEnum.RESOURCE_NOT_EXIST);
		}
		String thumbnailPath = webPageVideoTask.getThumbnailPath();
		thumbnailPath = ytDlpProperty.getDestination() + thumbnailPath;
		thumbnailPath = thumbnailPath.replace("\\", "/");

		log.info("图片路径: {}", thumbnailPath);
		Path path = Paths.get(thumbnailPath);
		httpServletRequest.setAttribute(ImageResourceHttpRequestHandler.ATTRIBUTE_FILE, path.toFile());
		try {
			imageResourceHttpRequestHandler.handleRequest(httpServletRequest, httpServletResponse);
		} catch (ServletException ignored) {
		}
	}

	@GetMapping("/video/{taskId}")
	public void getVideo(@PathVariable Long taskId, HttpServletRequest httpServletRequest,
																		 HttpServletResponse httpServletResponse) throws IOException {
		getVideo(taskId, null, httpServletRequest, httpServletResponse);
	}

	@GetMapping("/video/{taskId}/{videoId}")
	public void getVideo(@PathVariable Long taskId, @PathVariable(required = false) Long videoId, HttpServletRequest httpServletRequest,
																		 HttpServletResponse httpServletResponse) throws IOException {

		WebPageTask task = webPageTaskMapper.selectById(taskId);
		String path = "";
		if (task.getType().equals( WebPageTaskConstants.VIDEO_TYPE_SINGLE)) {
			LambdaQueryWrapper<WebPageVideoTask> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(WebPageVideoTask::getTaskId, task.getId());
			WebPageVideoTask webPageVideoTask = webPageVideoTaskMapper.selectOne(queryWrapper);
			path = ytDlpProperty.getDestination() + webPageVideoTask.getFilePath();
		}
		if (task.getType().equals(WebPageTaskConstants.VIDEO_TYPE_PLAYLIST)) {
			LambdaQueryWrapper<WebPageVideoTask> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(WebPageVideoTask::getTaskId, task.getId());
			if (!Objects.isNull(videoId)) {
				queryWrapper.eq(WebPageVideoTask::getId, videoId);
			} else {
				queryWrapper.eq(WebPageVideoTask::getVideoIndex, 1);
			}
			WebPageVideoTask webPageVideoTask = webPageVideoTaskMapper.selectOne(queryWrapper);
			path = ytDlpProperty.getDestination() + webPageVideoTask.getFilePath();
		}
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
