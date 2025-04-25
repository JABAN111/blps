package data

import (
	"bytes"
	"encoding/json"
	"fmt"
	"github.com/google/uuid"
	"github.com/stretchr/testify/require"
	"io"
	"net/http"
	"testing"
	"time"
)

const address = "http://localhost:8080"

const (
	apiV            = "/api/v1"
	authBase        = apiV + "/auth"
	userBase        = apiV + "/user"
	courseBase      = apiV + "/courses"
	registrationUrl = authBase + "/sign-up"
	login           = authBase + "/sign-in"
)

var client = http.Client{
	Timeout: 10 * time.Second,
}

func TestPreflight(t *testing.T) {
	require.Equal(t, true, true)
}

type RegistrationBody struct {
	FirstName   string    `json:"firstName"`
	LastName    string    `json:"lastName"`
	Email       string    `json:"email"`
	Password    string    `json:"password"`
	PhoneNumber string    `json:"phoneNumber"`
	CompanyName string    `json:"companyName,omitempty"`
	CourseUUID  uuid.UUID `json:"courseUUID"`
}

type LoginRequest struct {
	Email    string `json:"email"`
	Password string `json:"password"`
}

type JwtAuthenticationResponse struct {
	Token string `json:"token"`
}
type ApplicationResponseDto struct {
	Description string                    `json:"description"`
	Price       float64                   `json:"price"`
	Jwt         JwtAuthenticationResponse `json:"jwt"`
}

type ErrHttp struct {
	code int
	text string
}

func (e ErrHttp) Error() string {
	return fmt.Sprintf("{code: %v, error: %v}", e.code, e.text)
}

func (e ErrHttp) getCode() int {
	return e.code
}

func (e ErrHttp) getText() string {
	return e.text
}

type Course struct {
	CourseUUID     uuid.UUID   `json:"courseUUID"`
	CourseName     string      `json:"courseName"`
	CoursePrice    float64     `json:"coursePrice"`
	Description    string      `json:"description"`
	TopicName      string      `json:"topicName"`
	CreationDate   string      `json:"creationDate"`
	CourseDuration int         `json:"courseDuration"`
	WithJobOffer   bool        `json:"withJobOffer"`
	IsCompleted    interface{} `json:"isCompleted"`
}

type CoursesResponse struct {
	Courses []Course `json:"course_list"`
}

func getAllCourses(token string) ([]Course, error) {
	url := address + courseBase

	req, err := http.NewRequest(http.MethodGet, url, nil)
	if err != nil {
		return nil, fmt.Errorf("ошибка при создании запроса")
	}
	req.Header.Add("Authorization", "Bearer "+token)

	resp, err := client.Do(req)

	if err != nil {
		return nil, fmt.Errorf("ошибка выполнения запроса: %w", err)
	}

	if resp.StatusCode > 300 || resp.StatusCode < 200 {
		bodyBytes, _ := io.ReadAll(resp.Body)
		return nil, ErrHttp{
			code: resp.StatusCode,
			text: string(bodyBytes),
		}
	}

	var coursesResp CoursesResponse
	if err := json.NewDecoder(resp.Body).Decode(&coursesResp); err != nil {
		return nil, fmt.Errorf("ошибка при обработке ответа: %v", err)
	}

	return coursesResp.Courses, nil
}

func signUp(reg RegistrationBody) (*ApplicationResponseDto, error) {
	url := address + registrationUrl

	requestBody, err := json.Marshal(reg)
	if err != nil {
		return nil, fmt.Errorf("ошибка маршалинга запроса: %w", err)
	}

	req, err := http.NewRequest(http.MethodPost, url, bytes.NewBuffer(requestBody))
	if err != nil {
		return nil, fmt.Errorf("ошибка создания запроса: %w", err)
	}
	req.Header.Set("Content-Type", "application/json")

	resp, err := client.Do(req)
	if err != nil {
		return nil, fmt.Errorf("ошибка выполнения запроса: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode > 300 || resp.StatusCode < 200 {
		bodyBytes, _ := io.ReadAll(resp.Body)
		return nil, ErrHttp{
			code: resp.StatusCode,
			text: string(bodyBytes),
		}
	}

	var applicationResponse ApplicationResponseDto
	err = json.NewDecoder(resp.Body).Decode(&applicationResponse)
	if err != nil {
		return nil, fmt.Errorf("ошибка декодирования ответа: %w", err)
	}

	return &applicationResponse, nil
}

func signIn(reg LoginRequest) (*JwtAuthenticationResponse, error) {
	url := address + login

	requestBody, err := json.Marshal(reg)
	if err != nil {
		return nil, fmt.Errorf("ошибка маршалинга запроса: %w", err)
	}
	req, err := http.NewRequest(http.MethodPost, url, bytes.NewBuffer(requestBody))
	if err != nil {
		return nil, fmt.Errorf("ошибка создания запроса: %w", err)
	}
	req.Header.Set("Content-Type", "application/json")

	resp, err := client.Do(req)
	if err != nil {
		return nil, fmt.Errorf("ошибка выполнения запроса: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		bodyBytes, _ := io.ReadAll(resp.Body)
		return nil, fmt.Errorf("неожиданный код ответа: %d, тело: %s", resp.StatusCode, string(bodyBytes))
	}

	var jwtAuthResponse JwtAuthenticationResponse
	err = json.NewDecoder(resp.Body).Decode(&jwtAuthResponse)
	if err != nil {
		return nil, fmt.Errorf("ошибка декодирования ответа: %w", err)
	}

	return &jwtAuthResponse, nil
}

func createApplication(uuid uuid.UUID, token string) error {
	url := fmt.Sprintf("%s%s/application/%v", address, userBase, uuid)

	req, err := http.NewRequest(http.MethodPost, url, nil)
	if err != nil {
		return fmt.Errorf("ошибка создания запроса: %w", err)
	}
	req.Header.Add("Authorization", "Bearer "+token)

	resp, err := client.Do(req)

	if err != nil {
		return fmt.Errorf("ошибка выполнения запроса: %w", err)
	}

	if resp.StatusCode != http.StatusOK {
		bodyBytes, _ := io.ReadAll(resp.Body)
		return fmt.Errorf("неожиданный код ответа: %d, тело: %s", resp.StatusCode, string(bodyBytes))
	}

	return nil
}

func TestRegistrationWithoutCourseID(t *testing.T) {
	reg := RegistrationBody{
		FirstName:   "Ivan",
		LastName:    "Ivanov",
		Email:       uuid.NewString(),
		Password:    "secret",
		PhoneNumber: "+79001234567",
	}

	response, err := signUp(reg)
	require.NoError(t, err)
	require.NotNil(t, response)

	t.Logf("Registration response: %+v", response)
}

func TestLogin(t *testing.T) {
	type args struct {
		Email    string `json:"email"`
		Password string `json:"password"`
	}

	testCases := []struct {
		name   string
		args   args
		expErr bool
	}{
		{
			name: "valid test",
			args: args{
				Email:    "admin",
				Password: "admin",
			},
			expErr: false,
		},
		{
			name: "user not exist",
			args: args{
				Email:    "da",
				Password: "no",
			},
			expErr: true,
		},
		{
			name: "empty data",
			args: args{
				Email:    "",
				Password: "",
			},
			expErr: true,
		},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			reg := LoginRequest{
				Email:    tc.args.Email,
				Password: tc.args.Password,
			}
			res, err := signIn(reg)
			if tc.expErr {
				require.Error(t, err, tc.name)
			} else {
				require.NotNil(t, res, tc.name)
				require.True(t, len(res.Token) > 5)
			}
		})
	}
}

func TestCreateApplication(t *testing.T) {
	jwtResp, err := signIn(LoginRequest{Email: "jaba@jaba.jaba", Password: "jaba"})
	require.NoError(t, err, "failed to get token")
	token := jwtResp.Token
	t.Log("attempt to create an application with token: " + token)

	courses, err := getAllCourses(token)
	require.NoError(t, err, "failed to get courses")
	require.True(t, len(courses) != 0, "at least one course must be")

	testCases := []struct {
		name       string
		courseUUID uuid.UUID
		expErr     bool
	}{
		{
			name:       "Valid token creation",
			courseUUID: courses[0].CourseUUID, // NOTE: такой курс должен уже быть создан заранее
			expErr:     false,
		},
		{
			name: "course which are not exist",
			courseUUID: func() uuid.UUID {
				id, err := uuid.NewRandom()
				if err != nil {
					t.Log("fail to generate uuid, need to rerun test")
					panic(err)
				}
				return id
			}(),
			expErr: true,
		},
	}
	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			err := createApplication(tc.courseUUID, token)
			if tc.expErr {
				require.Error(t, err, tc.name)
			} else {
				require.NoError(t, err, tc.name)
			}
		})
	}
}

func TestGetCourses(t *testing.T) {
	jwtResp, err := signIn(LoginRequest{Email: "jaba@jaba.jaba", Password: "jaba"})
	require.NoError(t, err, "failed to get token")
	token := jwtResp.Token
	t.Log("attempt to create an application with token: " + token)
	_, err = getAllCourses(token)
	require.NoError(t, err)
}
