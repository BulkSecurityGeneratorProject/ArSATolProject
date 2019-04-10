package com.arsatoll.app.web.rest;

import com.arsatoll.app.ArsatollserviceApp;

import com.arsatoll.app.domain.Demo;
import com.arsatoll.app.repository.DemoRepository;
import com.arsatoll.app.service.DemoService;
import com.arsatoll.app.service.dto.DemoDTO;
import com.arsatoll.app.service.mapper.DemoMapper;
import com.arsatoll.app.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;


import static com.arsatoll.app.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the DemoResource REST controller.
 *
 * @see DemoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ArsatollserviceApp.class)
public class DemoResourceIntTest {

    private static final String DEFAULT_IMAGE_DEMO = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_DEMO = "BBBBBBBBBB";

    private static final String DEFAULT_AUDIO = "AAAAAAAAAA";
    private static final String UPDATED_AUDIO = "BBBBBBBBBB";

    private static final String DEFAULT_VIDEO_DEMO = "AAAAAAAAAA";
    private static final String UPDATED_VIDEO_DEMO = "BBBBBBBBBB";

    @Autowired
    private DemoRepository demoRepository;

    @Autowired
    private DemoMapper demoMapper;

    @Autowired
    private DemoService demoService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restDemoMockMvc;

    private Demo demo;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final DemoResource demoResource = new DemoResource(demoService);
        this.restDemoMockMvc = MockMvcBuilders.standaloneSetup(demoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Demo createEntity(EntityManager em) {
        Demo demo = new Demo()
            .imageDemo(DEFAULT_IMAGE_DEMO)
            .audio(DEFAULT_AUDIO)
            .videoDemo(DEFAULT_VIDEO_DEMO);
        return demo;
    }

    @Before
    public void initTest() {
        demo = createEntity(em);
    }

    @Test
    @Transactional
    public void createDemo() throws Exception {
        int databaseSizeBeforeCreate = demoRepository.findAll().size();

        // Create the Demo
        DemoDTO demoDTO = demoMapper.toDto(demo);
        restDemoMockMvc.perform(post("/api/demos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(demoDTO)))
            .andExpect(status().isCreated());

        // Validate the Demo in the database
        List<Demo> demoList = demoRepository.findAll();
        assertThat(demoList).hasSize(databaseSizeBeforeCreate + 1);
        Demo testDemo = demoList.get(demoList.size() - 1);
        assertThat(testDemo.getImageDemo()).isEqualTo(DEFAULT_IMAGE_DEMO);
        assertThat(testDemo.getAudio()).isEqualTo(DEFAULT_AUDIO);
        assertThat(testDemo.getVideoDemo()).isEqualTo(DEFAULT_VIDEO_DEMO);
    }

    @Test
    @Transactional
    public void createDemoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = demoRepository.findAll().size();

        // Create the Demo with an existing ID
        demo.setId(1L);
        DemoDTO demoDTO = demoMapper.toDto(demo);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDemoMockMvc.perform(post("/api/demos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(demoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Demo in the database
        List<Demo> demoList = demoRepository.findAll();
        assertThat(demoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllDemos() throws Exception {
        // Initialize the database
        demoRepository.saveAndFlush(demo);

        // Get all the demoList
        restDemoMockMvc.perform(get("/api/demos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(demo.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageDemo").value(hasItem(DEFAULT_IMAGE_DEMO.toString())))
            .andExpect(jsonPath("$.[*].audio").value(hasItem(DEFAULT_AUDIO.toString())))
            .andExpect(jsonPath("$.[*].videoDemo").value(hasItem(DEFAULT_VIDEO_DEMO.toString())));
    }
    
    @Test
    @Transactional
    public void getDemo() throws Exception {
        // Initialize the database
        demoRepository.saveAndFlush(demo);

        // Get the demo
        restDemoMockMvc.perform(get("/api/demos/{id}", demo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(demo.getId().intValue()))
            .andExpect(jsonPath("$.imageDemo").value(DEFAULT_IMAGE_DEMO.toString()))
            .andExpect(jsonPath("$.audio").value(DEFAULT_AUDIO.toString()))
            .andExpect(jsonPath("$.videoDemo").value(DEFAULT_VIDEO_DEMO.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingDemo() throws Exception {
        // Get the demo
        restDemoMockMvc.perform(get("/api/demos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDemo() throws Exception {
        // Initialize the database
        demoRepository.saveAndFlush(demo);

        int databaseSizeBeforeUpdate = demoRepository.findAll().size();

        // Update the demo
        Demo updatedDemo = demoRepository.findById(demo.getId()).get();
        // Disconnect from session so that the updates on updatedDemo are not directly saved in db
        em.detach(updatedDemo);
        updatedDemo
            .imageDemo(UPDATED_IMAGE_DEMO)
            .audio(UPDATED_AUDIO)
            .videoDemo(UPDATED_VIDEO_DEMO);
        DemoDTO demoDTO = demoMapper.toDto(updatedDemo);

        restDemoMockMvc.perform(put("/api/demos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(demoDTO)))
            .andExpect(status().isOk());

        // Validate the Demo in the database
        List<Demo> demoList = demoRepository.findAll();
        assertThat(demoList).hasSize(databaseSizeBeforeUpdate);
        Demo testDemo = demoList.get(demoList.size() - 1);
        assertThat(testDemo.getImageDemo()).isEqualTo(UPDATED_IMAGE_DEMO);
        assertThat(testDemo.getAudio()).isEqualTo(UPDATED_AUDIO);
        assertThat(testDemo.getVideoDemo()).isEqualTo(UPDATED_VIDEO_DEMO);
    }

    @Test
    @Transactional
    public void updateNonExistingDemo() throws Exception {
        int databaseSizeBeforeUpdate = demoRepository.findAll().size();

        // Create the Demo
        DemoDTO demoDTO = demoMapper.toDto(demo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDemoMockMvc.perform(put("/api/demos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(demoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Demo in the database
        List<Demo> demoList = demoRepository.findAll();
        assertThat(demoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteDemo() throws Exception {
        // Initialize the database
        demoRepository.saveAndFlush(demo);

        int databaseSizeBeforeDelete = demoRepository.findAll().size();

        // Delete the demo
        restDemoMockMvc.perform(delete("/api/demos/{id}", demo.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Demo> demoList = demoRepository.findAll();
        assertThat(demoList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Demo.class);
        Demo demo1 = new Demo();
        demo1.setId(1L);
        Demo demo2 = new Demo();
        demo2.setId(demo1.getId());
        assertThat(demo1).isEqualTo(demo2);
        demo2.setId(2L);
        assertThat(demo1).isNotEqualTo(demo2);
        demo1.setId(null);
        assertThat(demo1).isNotEqualTo(demo2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DemoDTO.class);
        DemoDTO demoDTO1 = new DemoDTO();
        demoDTO1.setId(1L);
        DemoDTO demoDTO2 = new DemoDTO();
        assertThat(demoDTO1).isNotEqualTo(demoDTO2);
        demoDTO2.setId(demoDTO1.getId());
        assertThat(demoDTO1).isEqualTo(demoDTO2);
        demoDTO2.setId(2L);
        assertThat(demoDTO1).isNotEqualTo(demoDTO2);
        demoDTO1.setId(null);
        assertThat(demoDTO1).isNotEqualTo(demoDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(demoMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(demoMapper.fromId(null)).isNull();
    }
}
