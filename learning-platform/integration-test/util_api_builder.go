package data

import (
	"bytes"
	"encoding/json"
	"fmt"
	"github.com/stretchr/testify/require"
	"io"
	"net/http"
	"strconv"
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
	Timeout: 10 * time.Minute,
}

func TestPreflight(t *testing.T) {
	require.Equal(t, true, true)
}

type RegistrationBody struct {
	FirstName   string `json:"firstName"`
	LastName    string `json:"lastName"`
	Email       string `json:"email"`
	Password    string `json:"password"`
	PhoneNumber string `json:"phoneNumber"`
	CompanyName string `json:"companyName,omitempty"`
	CourseUUID  int    `json:"courseId"`
}

type LoginRequest struct {
	Email    string `json:"email"`
	Password string `json:"password"`
}

type JwtAuthenticationResponse struct {
	Token string `json:"token"`
}
type ApplicationResponseDto struct {
	Description   string                    `json:"description"`
	Price         float64                   `json:"price"`
	ApplicationID int                       `json:"applicationID"`
	Jwt           JwtAuthenticationResponse `json:"jwt"`
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
	CourseID       int         `json:"courseID"`
	CourseName     string      `json:"courseName"`
	CoursePrice    float64     `json:"coursePrice"`
	Description    string      `json:"description"`
	TopicName      string      `json:"topicName"`
	CreationDate   []int       `json:"creationDate"`
	CourseDuration int         `json:"courseDuration"`
	WithJobOffer   bool        `json:"withJobOffer"`
	IsCompleted    interface{} `json:"isCompleted"`
}

type CoursesResponse struct {
	Courses []Course `json:"course_list"`
}

func getAllCourses() ([]Course, error) {
	url := address + courseBase

	req, err := http.NewRequest(http.MethodGet, url, nil)
	if err != nil {
		return nil, fmt.Errorf("ошибка при создании запроса")
	}

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

func createCourse(token string, course Course) error {
	url := address + courseBase

	reqBody, err := json.Marshal(course)
	if err != nil {
		return fmt.Errorf("ошибка маршалинга запроса: %w", err)
	}
	req, err := http.NewRequest(http.MethodPost, url, bytes.NewBuffer(reqBody))
	if err != nil {
		return fmt.Errorf("ошибка создания запроса: %w", err)
	}
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Accept", "application/json")
	req.Header.Set("Authorization", "Bearer "+token)

	resp, err := client.Do(req)
	if err != nil {
		return fmt.Errorf("ошибка выполнения запроса: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode < 200 || resp.StatusCode > 300 {
		bodyBytes, _ := io.ReadAll(resp.Body)
		var httpErr ErrHttp
		httpErr.code = resp.StatusCode
		httpErr.text = string(bodyBytes)
		return httpErr
	}

	return nil
}

func updateCourse(token string, course Course, courseID int) error {
	url := address + courseBase + "/" + strconv.Itoa(courseID)

	reqBody, err := json.Marshal(course)
	if err != nil {
		return fmt.Errorf("ошибка маршалинга запроса: %w", err)
	}
	req, err := http.NewRequest(http.MethodPut, url, bytes.NewBuffer(reqBody))
	if err != nil {
		return fmt.Errorf("ошибка создания запроса: %w", err)
	}
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Accept", "application/json")
	req.Header.Set("Authorization", "Bearer "+token)

	resp, err := client.Do(req)
	if err != nil {
		return fmt.Errorf("ошибка выполнения запроса: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode < 200 || resp.StatusCode > 300 {
		bodyBytes, _ := io.ReadAll(resp.Body)
		var httpErr ErrHttp
		httpErr.code = resp.StatusCode
		httpErr.text = string(bodyBytes)
		return httpErr
	}

	return nil
}

func addAdditionalCourse(token string, mainCourseID, additionalCourseID int) (Course, error) {
	url := address + courseBase + "/" + strconv.Itoa(mainCourseID) + "/additional/" + strconv.Itoa(additionalCourseID)

	req, err := http.NewRequest(http.MethodPost, url, nil)
	if err != nil {
		return Course{}, fmt.Errorf("ошибка создания запроса: %w", err)
	}
	req.Header.Set("Authorization", "Bearer "+token)
	resp, err := client.Do(req)
	if err != nil {
		return Course{}, fmt.Errorf("ошибка выполнения запроса: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode < 200 || resp.StatusCode > 300 {
		bodyBytes, _ := io.ReadAll(resp.Body)
		var httpErr ErrHttp
		httpErr.code = resp.StatusCode
		httpErr.text = string(bodyBytes)
		return Course{}, httpErr
	}

	type innerCourse struct {
		Course Course `json:"course"`
	}
	var res innerCourse
	if err := json.NewDecoder(resp.Body).Decode(&res); err != nil {
		return Course{}, err
	}
	return res.Course, nil
}

func signUp(reg RegistrationBody) (*JwtAuthenticationResponse, error) {
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

	var applicationResponse JwtAuthenticationResponse
	err = json.NewDecoder(resp.Body).Decode(&applicationResponse)
	if err != nil {
		return nil, fmt.Errorf("ошибка декодирования ответа: %w", err)
	}

	return &applicationResponse, nil
}

func getCourse(courseID int) (Course, error) {
	url := address + courseBase + "/id/" + strconv.Itoa(courseID)

	req, err := http.NewRequest(http.MethodGet, url, nil)
	if err != nil {
		return Course{}, fmt.Errorf("ошибка при создании запроса")
	}

	resp, err := client.Do(req)

	if err != nil {
		return Course{}, fmt.Errorf("ошибка выполнения запроса: %w", err)
	}

	if resp.StatusCode > 300 || resp.StatusCode < 200 {
		bodyBytes, _ := io.ReadAll(resp.Body)
		return Course{}, ErrHttp{
			code: resp.StatusCode,
			text: string(bodyBytes),
		}
	}

	type wrapper struct {
		Course Course `json:"course"`
	}
	var wrap wrapper
	if err := json.NewDecoder(resp.Body).Decode(&wrap); err != nil {
		return Course{}, fmt.Errorf("ошибка при обработке ответа: %v", err)
	}

	return wrap.Course, nil

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

	if resp.StatusCode < 200 || resp.StatusCode > 300 {
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

func createApplication(uuid int, token string) (int, error) {
	var errHttp ErrHttp
	url := fmt.Sprintf("%s%s/application/%v", address, userBase, uuid)

	req, err := http.NewRequest(http.MethodPost, url, nil)
	if err != nil {
		return 0, fmt.Errorf("ошибка создания запроса: %w", err)
	}
	req.Header.Add("Authorization", "Bearer "+token)

	resp, err := client.Do(req)

	if err != nil {
		return 0, fmt.Errorf("ошибка выполнения запроса: %w", err)
	}

	if resp.StatusCode != http.StatusOK {
		bodyBytes, _ := io.ReadAll(resp.Body)
		errHttp.code = resp.StatusCode
		errHttp.text = string(bodyBytes)
		return 0, errHttp
	}
	bodyBytes, _ := io.ReadAll(resp.Body)

	id, _ := strconv.Atoi(string(bodyBytes))
	return id, nil
}

func updateApplicationStatus(applicationID int, token, newStatus string) error {
	url := address + userBase + "/application/status/" + strconv.Itoa(applicationID)

	type updApplStatusBody struct {
		NewStatus string `json:"newStatus"`
	}
	stat := updApplStatusBody{NewStatus: newStatus}

	body, err := json.Marshal(stat)
	if err != nil {
		return err
	}

	req, err := http.NewRequest(http.MethodPatch, url, bytes.NewBuffer(body))

	if err != nil {
		return fmt.Errorf("ошибка при создании запроса")
	}
	req.Header.Set("Content-Type", "application/json")
	req.Header.Add("Authorization", "Bearer "+token)
	resp, err := client.Do(req)

	if err != nil {
		return fmt.Errorf("ошибка выполнения запроса: %w", err)
	}

	if resp.StatusCode > 300 || resp.StatusCode < 200 {
		bodyBytes, _ := io.ReadAll(resp.Body)
		return ErrHttp{
			code: resp.StatusCode,
			text: string(bodyBytes),
		}
	}

	return nil
}

func deleteCourse(token string, courseId int) error {
	url := address + courseBase + "/" + strconv.Itoa(courseId)

	reg, _ := http.NewRequest(http.MethodDelete, url, nil)
	reg.Header.Set("Authorization", "Bearer "+token)

	resp, err := client.Do(reg)

	if err != nil {
		return fmt.Errorf("ошибка выполнения запроса: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode < 200 || resp.StatusCode > 300 {
		bodyBytes, _ := io.ReadAll(resp.Body)
		var httpErr ErrHttp
		httpErr.code = resp.StatusCode
		httpErr.text = string(bodyBytes)
		return httpErr
	}

	return nil
}

func generateNewUserAndToken(email, password string) (string, error) {
	app, err := signUp(
		RegistrationBody{
			FirstName:   email,
			LastName:    email,
			Email:       email + "@gmail.com",
			Password:    password,
			PhoneNumber: "+78005553535",
		},
	)
	if err != nil {
		return "", err
	}
	return app.Token, nil
}

func generateToken(email, password string) (string, error) {
	jwtToken, err := signIn(LoginRequest{
		Email:    email,
		Password: password,
	})
	if err != nil {
		return "", err
	}
	return jwtToken.Token, nil
}

func getExistCourseID() int {
	res, err := getAllCourses()
	if err != nil {
		return 0
	}
	if len(res) == 0 {
		return 0
	}

	return res[0].CourseID
}

func getCourseByName(courseName string) (Course, error) {
	url := address + courseBase + "/name/" + courseName

	req, err := http.NewRequest(http.MethodGet, url, nil)
	if err != nil {
		return Course{}, fmt.Errorf("ошибка при создании запроса")
	}

	resp, err := client.Do(req)

	if err != nil {
		return Course{}, fmt.Errorf("ошибка выполнения запроса: %w", err)
	}

	if resp.StatusCode > 300 || resp.StatusCode < 200 {
		bodyBytes, _ := io.ReadAll(resp.Body)
		return Course{}, ErrHttp{
			code: resp.StatusCode,
			text: string(bodyBytes),
		}
	}
	type innerCourse struct {
		Course Course `json:"course"`
	}

	var res innerCourse
	if err := json.NewDecoder(resp.Body).Decode(&res); err != nil {
		return Course{}, fmt.Errorf("ошибка при обработке ответа: %v", err)
	}
	return res.Course, nil
}

func enrollUser(token string, userID, courseID int) error {
	url := address + "/api/v1/enrollment/enroll"

	req, err := http.NewRequest(http.MethodPost, url, nil)
	if err != nil {
		return fmt.Errorf("ошибка при создании запроса: %w", err)
	}

	q := req.URL.Query()
	q.Add("userId", strconv.Itoa(userID))
	q.Add("courseID", strconv.Itoa(courseID))
	req.URL.RawQuery = q.Encode()

	req.Header.Set("Authorization", "Bearer "+token)

	resp, err := client.Do(req)
	if err != nil {
		return fmt.Errorf("ошибка выполнения запроса: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode < 200 || resp.StatusCode > 299 {
		bodyBytes, _ := io.ReadAll(resp.Body)
		return ErrHttp{
			code: resp.StatusCode,
			text: string(bodyBytes),
		}
	}

	return nil
}

// creating exercise and other shit
type ExerciseDto struct {
	Name            string `json:"name"`
	Description     string `json:"description"`
	ModuleId        int    `json:"moduleId"`
	DifficultyLevel string `json:"difficultyLevel"`
	Answer          string `json:"answer"`
}

type ModuleEntity struct {
	Name        string `json:"name"`
	IsCompleted bool   `json:"isCompleted"`
	OrderNumber int    `json:"orderNumber"`
	Description string `json:"description"`
	Course      struct {
		CourseId int `json:"courseId"`
	} `json:"course"`
	ModuleExercises []interface{} `json:"moduleExercises"`
}

func getModules(token string, courseID int) ([]ModuleEntity, error) {
	url := address + "/api/v1/modules/course/" + strconv.Itoa(courseID)

	reg, _ := http.NewRequest(http.MethodGet, url, nil)
	reg.Header.Add("Authorization", "Bearer "+token)
	resp, err := client.Do(reg)
	if err != nil {
		return nil, err
	}

	if resp.StatusCode < 200 || resp.StatusCode > 299 {
		bodyBytes, _ := io.ReadAll(resp.Body)
		return nil, ErrHttp{
			code: resp.StatusCode,
			text: string(bodyBytes),
		}
	}

	type wrap struct {
		Modules []ModuleEntity `json:"modules_list"`
	}
	var result wrap

	err = json.NewDecoder(resp.Body).Decode(&result)
	if err != nil {
		return nil, err
	}
	return result.Modules, nil
}

func createModule(token string, module ModuleEntity) error {
	url := address + "/api/v1/modules"

	body, err := json.Marshal(module)
	if err != nil {
		return err
	}

	reg, _ := http.NewRequest(http.MethodPost, url, bytes.NewBuffer(body))
	reg.Header.Set("Content-Type", "application/json")
	reg.Header.Add("Authorization", "Bearer "+token)

	resp, err := client.Do(reg)

	if err != nil {
		return fmt.Errorf("ошибка выполнения запроса: %w", err)
	}

	if resp.StatusCode > 300 || resp.StatusCode < 200 {
		bodyBytes, _ := io.ReadAll(resp.Body)
		return ErrHttp{
			code: resp.StatusCode,
			text: string(bodyBytes),
		}
	}

	return nil
}
