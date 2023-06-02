package com.zelusik.scraping.logger.filter;

import org.springframework.util.StreamUtils;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RequestWrapper extends HttpServletRequestWrapper {

    private final byte[] cachedInputStream;

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.cachedInputStream = StreamUtils.copyToByteArray(request.getInputStream());
    }

    @Override
    public ServletInputStream getInputStream() {
        return new ServletInputStream() {
            private InputStream cachedBodyInputStream = new ByteArrayInputStream(cachedInputStream);

            @Override
            public boolean isFinished() {
                try {
                    return cachedBodyInputStream.available() == 0;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int read() throws IOException {
                return cachedBodyInputStream.read();
            }
        };
    }
}
