package org.magnum.dataup.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;

import org.magnum.dataup.VideoFileManager;
import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.magnum.dataup.model.VideoStatus.VideoState;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/video")
public class VideoController {
  private final VideoFileManager manag;
  
  public VideoController() throws IOException {
    manag = VideoFileManager.get();
  }
  
  @GetMapping
  public Collection<Video> getAllVideos() {   
    return manag.getAllVideos();
  }
  
  @PostMapping
  public Video addVideo(@RequestBody Video v) {
    return manag.addVideo(v);
  }
  
  @PostMapping(path = "/{id}/data")
  public VideoStatus addVideoData(@PathVariable long id, @RequestPart MultipartFile file) throws IOException {
    var v = manag.getVideo(id);
    if (v == null)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    var in = file.getInputStream();
    manag.saveVideoData(v, in);
    return new VideoStatus(VideoState.READY);
  }
  
  @GetMapping(path = "/{id}/data")
  public byte[] getVideoData(@PathVariable long id) throws IOException {
    var v = manag.getVideo(id);
    if (v == null || !manag.hasVideoData(v))
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    var b = new ByteArrayOutputStream();
    manag.copyVideoData(v, b);
    return b.toByteArray();
  }
  
}
