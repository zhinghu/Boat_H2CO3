/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.system.macosx;

import static org.lwjgl.system.Checks.CHECKS;
import static org.lwjgl.system.Checks.check;
import static org.lwjgl.system.Checks.checkNT1;
import static org.lwjgl.system.MemoryStack.stackGet;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memByteBufferNT1;
import static org.lwjgl.system.MemoryUtil.memCopy;
import static org.lwjgl.system.MemoryUtil.memGetAddress;
import static org.lwjgl.system.MemoryUtil.memPutAddress;
import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.system.MemoryUtil.nmemAllocChecked;
import static org.lwjgl.system.MemoryUtil.nmemCallocChecked;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

import java.nio.ByteBuffer;

import javax.annotation.Nullable;

/**
 * Defines a property attribute.
 * 
 * <h3>Layout</h3>
 * 
 * <pre><code>
 * struct objc_property_attribute_t {
 *     char * {@link #name};
 *     char * {@link #value};
 * }</code></pre>
 */
@NativeType("struct objc_property_attribute_t")
public class ObjCPropertyAttribute extends Struct<ObjCPropertyAttribute> implements NativeResource {

    /** The struct size in bytes. */
    public static final int SIZEOF;

    /** The struct alignment in bytes. */
    public static final int ALIGNOF;

    /** The struct member offsets. */
    public static final int
        NAME,
        VALUE;

    static {
        Layout layout = __struct(
            __member(POINTER_SIZE),
            __member(POINTER_SIZE)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        NAME = layout.offsetof(0);
        VALUE = layout.offsetof(1);
    }

    protected ObjCPropertyAttribute(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected ObjCPropertyAttribute create(long address, @Nullable ByteBuffer container) {
        return new ObjCPropertyAttribute(address, container);
    }

    /**
     * Creates a {@code ObjCPropertyAttribute} instance at the current position of the specified {@link ByteBuffer} container. Changes to the buffer's content will be
     * visible to the struct instance and vice versa.
     *
     * <p>The created instance holds a strong reference to the container object.</p>
     */
    public ObjCPropertyAttribute(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() { return SIZEOF; }

    /** the name of the attribute */
    @NativeType("char *")
    public ByteBuffer name() { return nname(address()); }
    /** the name of the attribute */
    @NativeType("char *")
    public String nameString() { return nnameString(address()); }
    /** the value of the attribute (usually empty) */
    @NativeType("char *")
    public ByteBuffer value() { return nvalue(address()); }
    /** the value of the attribute (usually empty) */
    @NativeType("char *")
    public String valueString() { return nvalueString(address()); }

    /** Sets the address of the specified encoded string to the {@link #name} field. */
    public ObjCPropertyAttribute name(@NativeType("char *") ByteBuffer value) { nname(address(), value); return this; }
    /** Sets the address of the specified encoded string to the {@link #value} field. */
    public ObjCPropertyAttribute value(@NativeType("char *") ByteBuffer value) { nvalue(address(), value); return this; }

    /** Initializes this struct with the specified values. */
    public ObjCPropertyAttribute set(
        ByteBuffer name,
        ByteBuffer value
    ) {
        name(name);
        value(value);

        return this;
    }

    /**
     * Copies the specified struct data to this struct.
     *
     * @param src the source struct
     *
     * @return this struct
     */
    public ObjCPropertyAttribute set(ObjCPropertyAttribute src) {
        memCopy(src.address(), address(), SIZEOF);
        return this;
    }

    // -----------------------------------

    /** Returns a new {@code ObjCPropertyAttribute} instance allocated with {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed. */
    public static ObjCPropertyAttribute malloc() {
        return new ObjCPropertyAttribute(nmemAllocChecked(SIZEOF), null);
    }

    /** Returns a new {@code ObjCPropertyAttribute} instance allocated with {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly freed. */
    public static ObjCPropertyAttribute calloc() {
        return new ObjCPropertyAttribute(nmemCallocChecked(1, SIZEOF), null);
    }

    /** Returns a new {@code ObjCPropertyAttribute} instance allocated with {@link BufferUtils}. */
    public static ObjCPropertyAttribute create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new ObjCPropertyAttribute(memAddress(container), container);
    }

    /** Returns a new {@code ObjCPropertyAttribute} instance for the specified memory address. */
    public static ObjCPropertyAttribute create(long address) {
        return new ObjCPropertyAttribute(address, null);
    }

    /** Like {@link #create(long) create}, but returns {@code null} if {@code address} is {@code NULL}. */
    @Nullable
    public static ObjCPropertyAttribute createSafe(long address) {
        return address == NULL ? null : new ObjCPropertyAttribute(address, null);
    }

    /**
     * Returns a new {@link Buffer} instance allocated with {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed.
     *
     * @param capacity the buffer capacity
     */
    public static Buffer malloc(int capacity) {
        return new Buffer(nmemAllocChecked(__checkMalloc(capacity, SIZEOF)), capacity);
    }

    /**
     * Returns a new {@link Buffer} instance allocated with {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly freed.
     *
     * @param capacity the buffer capacity
     */
    public static Buffer calloc(int capacity) {
        return new Buffer(nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    /**
     * Returns a new {@link Buffer} instance allocated with {@link BufferUtils}.
     *
     * @param capacity the buffer capacity
     */
    public static Buffer create(int capacity) {
        ByteBuffer container = __create(capacity, SIZEOF);
        return new Buffer(memAddress(container), container, -1, 0, capacity, capacity);
    }

    /**
     * Create a {@link Buffer} instance at the specified memory.
     *
     * @param address  the memory address
     * @param capacity the buffer capacity
     */
    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    /** Like {@link #create(long, int) create}, but returns {@code null} if {@code address} is {@code NULL}. */
    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == NULL ? null : new Buffer(address, capacity);
    }

    // -----------------------------------

    /** Deprecated for removal in 3.4.0. Use {@link #malloc(MemoryStack)} instead. */
    @Deprecated public static ObjCPropertyAttribute mallocStack() { return malloc(stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(MemoryStack)} instead. */
    @Deprecated public static ObjCPropertyAttribute callocStack() { return calloc(stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #malloc(MemoryStack)} instead. */
    @Deprecated public static ObjCPropertyAttribute mallocStack(MemoryStack stack) { return malloc(stack); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(MemoryStack)} instead. */
    @Deprecated public static ObjCPropertyAttribute callocStack(MemoryStack stack) { return calloc(stack); }
    /** Deprecated for removal in 3.4.0. Use {@link #malloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer mallocStack(int capacity) { return malloc(capacity, stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer callocStack(int capacity) { return calloc(capacity, stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #malloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer mallocStack(int capacity, MemoryStack stack) { return malloc(capacity, stack); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer callocStack(int capacity, MemoryStack stack) { return calloc(capacity, stack); }

    /**
     * Returns a new {@code ObjCPropertyAttribute} instance allocated on the specified {@link MemoryStack}.
     *
     * @param stack the stack from which to allocate
     */
    public static ObjCPropertyAttribute malloc(MemoryStack stack) {
        return new ObjCPropertyAttribute(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    /**
     * Returns a new {@code ObjCPropertyAttribute} instance allocated on the specified {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack the stack from which to allocate
     */
    public static ObjCPropertyAttribute calloc(MemoryStack stack) {
        return new ObjCPropertyAttribute(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    /**
     * Returns a new {@link Buffer} instance allocated on the specified {@link MemoryStack}.
     *
     * @param stack    the stack from which to allocate
     * @param capacity the buffer capacity
     */
    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    /**
     * Returns a new {@link Buffer} instance allocated on the specified {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack    the stack from which to allocate
     * @param capacity the buffer capacity
     */
    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    // -----------------------------------

    /** Unsafe version of {@link #name}. */
    public static ByteBuffer nname(long struct) { return memByteBufferNT1(memGetAddress(struct + ObjCPropertyAttribute.NAME)); }
    /** Unsafe version of {@link #nameString}. */
    public static String nnameString(long struct) { return memUTF8(memGetAddress(struct + ObjCPropertyAttribute.NAME)); }
    /** Unsafe version of {@link #value}. */
    public static ByteBuffer nvalue(long struct) { return memByteBufferNT1(memGetAddress(struct + ObjCPropertyAttribute.VALUE)); }
    /** Unsafe version of {@link #valueString}. */
    public static String nvalueString(long struct) { return memUTF8(memGetAddress(struct + ObjCPropertyAttribute.VALUE)); }

    /** Unsafe version of {@link #name(ByteBuffer) name}. */
    public static void nname(long struct, ByteBuffer value) {
        if (CHECKS) { checkNT1(value); }
        memPutAddress(struct + ObjCPropertyAttribute.NAME, memAddress(value));
    }
    /** Unsafe version of {@link #value(ByteBuffer) value}. */
    public static void nvalue(long struct, ByteBuffer value) {
        if (CHECKS) { checkNT1(value); }
        memPutAddress(struct + ObjCPropertyAttribute.VALUE, memAddress(value));
    }

    /**
     * Validates pointer members that should not be {@code NULL}.
     *
     * @param struct the struct to validate
     */
    public static void validate(long struct) {
        check(memGetAddress(struct + ObjCPropertyAttribute.NAME));
        check(memGetAddress(struct + ObjCPropertyAttribute.VALUE));
    }

    // -----------------------------------

    /** An array of {@link ObjCPropertyAttribute} structs. */
    public static class Buffer extends StructBuffer<ObjCPropertyAttribute, Buffer> implements NativeResource {

        private static final ObjCPropertyAttribute ELEMENT_FACTORY = ObjCPropertyAttribute.create(-1L);

        /**
         * Creates a new {@code ObjCPropertyAttribute.Buffer} instance backed by the specified container.
         *
         * <p>Changes to the container's content will be visible to the struct buffer instance and vice versa. The two buffers' position, limit, and mark values
         * will be independent. The new buffer's position will be zero, its capacity and its limit will be the number of bytes remaining in this buffer divided
         * by {@link ObjCPropertyAttribute#SIZEOF}, and its mark will be undefined.</p>
         *
         * <p>The created buffer instance holds a strong reference to the container object.</p>
         */
        public Buffer(ByteBuffer container) {
            super(container, container.remaining() / SIZEOF);
        }

        public Buffer(long address, int cap) {
            super(address, null, -1, 0, cap, cap);
        }

        Buffer(long address, @Nullable ByteBuffer container, int mark, int pos, int lim, int cap) {
            super(address, container, mark, pos, lim, cap);
        }

        @Override
        protected Buffer self() {
            return this;
        }

        @Override
        protected ObjCPropertyAttribute getElementFactory() {
            return ELEMENT_FACTORY;
        }

        /** @return a {@link ByteBuffer} view of the null-terminated string pointed to by the {@link ObjCPropertyAttribute#name} field. */
        @NativeType("char *")
        public ByteBuffer name() { return ObjCPropertyAttribute.nname(address()); }
        /** @return the null-terminated string pointed to by the {@link ObjCPropertyAttribute#name} field. */
        @NativeType("char *")
        public String nameString() { return ObjCPropertyAttribute.nnameString(address()); }
        /** @return a {@link ByteBuffer} view of the null-terminated string pointed to by the {@link ObjCPropertyAttribute#value} field. */
        @NativeType("char *")
        public ByteBuffer value() { return ObjCPropertyAttribute.nvalue(address()); }
        /** @return the null-terminated string pointed to by the {@link ObjCPropertyAttribute#value} field. */
        @NativeType("char *")
        public String valueString() { return ObjCPropertyAttribute.nvalueString(address()); }

        /** Sets the address of the specified encoded string to the {@link ObjCPropertyAttribute#name} field. */
        public Buffer name(@NativeType("char *") ByteBuffer value) { ObjCPropertyAttribute.nname(address(), value); return this; }
        /** Sets the address of the specified encoded string to the {@link ObjCPropertyAttribute#value} field. */
        public Buffer value(@NativeType("char *") ByteBuffer value) { ObjCPropertyAttribute.nvalue(address(), value); return this; }

    }

}