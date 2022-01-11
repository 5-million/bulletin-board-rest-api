package xyz.fivemillion.bulletinboardapi.config.web;

public class PageRequest implements Pageable {

    public static final long DEFAULT_OFFSET_VALUE = 0;
    public static final long DEFAULT_SIZE_VALUE = 100;

    private final long offset;
    private final long size;

    public PageRequest() {
        this(DEFAULT_OFFSET_VALUE, DEFAULT_SIZE_VALUE);
    }

    public PageRequest(Long offset, Long size) {
        this.offset = offset == null ? DEFAULT_OFFSET_VALUE : offset;
        this.size = size == null ? DEFAULT_SIZE_VALUE : size;
    }

    @Override
    public Long getOffset() {
        return this.offset;
    }

    @Override
    public Long getSize() {
        return this.size;
    }
}
