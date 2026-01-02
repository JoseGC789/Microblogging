package com.github.josegc789.microblogging.spi;

import com.github.josegc789.microblogging.core.NewPost;

public interface PostSpi {

  String publish(NewPost newPost);
}
