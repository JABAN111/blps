package data

import (
	"fmt"
	"github.com/google/uuid"
	"github.com/stretchr/testify/require"
	"testing"
)

func TestCreateCourse(t *testing.T) {

	testCases := []struct {
		name   string
		course Course
		expErr bool
	}{
		{
			name: "successful creation",
			course: Course{
				CourseName:     "skillbox",
				CoursePrice:    2341,
				Description:    "cool text of bullshit",
				TopicName:      "MARKETING",
				CourseDuration: 23,

				WithJobOffer: false,
			},
			expErr: false,
		},
	}
	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			token, err := generateToken("admin@admin.admin", "admin")
			require.NoError(t, err)

			coursesBeforeUpdate, err := getAllCourses()
			require.NoError(t, err, "fail to get reguest of all courses")
			cntCoursesBeforeUpdate := len(coursesBeforeUpdate)

			err = createCourse(token, tc.course)
			coursesAfterUpdate, err := getAllCourses()
			require.NoError(t, err, "fail to get reguest of all courses")
			cntCoursesAfterUpdate := len(coursesAfterUpdate)
			if tc.expErr {
				require.Equal(t, cntCoursesBeforeUpdate, cntCoursesAfterUpdate, "expect on fail no new courses")
				require.Error(t, err)
			} else {
				require.Equal(t, cntCoursesAfterUpdate, cntCoursesBeforeUpdate+1, "expect new course after creating")
				require.NoError(t, err)
			}
		})
	}
}

func TestDeleteCourse(t *testing.T) {

	testCases := []struct {
		name     string
		courseID int
		expErr   bool
	}{
		{
			name:     "successful test",
			courseID: getExistCourseID(),
			expErr:   false,
		},
		{
			name:     "invalid id",
			courseID: -1,
			expErr:   true,
		},
		{
			name:     "course which are not exist",
			courseID: getExistCourseID() * 200,
			expErr:   true,
		},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			token, err := generateToken("admin@admin.admin", "admin")
			require.NoError(t, err)

			coursesBeforeUpdate, err := getAllCourses()
			require.NoError(t, err, "fail to get reguest of all courses")
			cntCoursesBeforeUpdate := len(coursesBeforeUpdate)

			err = deleteCourse(token, tc.courseID)
			coursesAfterUpdate, _ := getAllCourses()
			cntCoursesAfterUpdate := len(coursesAfterUpdate)

			if tc.expErr {
				require.Error(t, err)
				require.Equal(t, cntCoursesBeforeUpdate, cntCoursesAfterUpdate, "expect on fail no new courses")
			} else {
				require.NoError(t, err)
				require.Equal(t, cntCoursesAfterUpdate, cntCoursesBeforeUpdate-1, "expect new course after creating")
			}
		})
	}
}

func TestCreateCourseWithAdditionals(t *testing.T) {
	testCases := []struct {
		name          string
		newCourse     Course
		additionalIDs []int
		expErr        bool
	}{
		{
			name: "creating course with one additional",
			newCourse: Course{
				CourseName:     uuid.NewString(),
				CoursePrice:    888,
				Description:    "Only python develop another python developer",
				TopicName:      "MANAGEMENT",
				CourseDuration: 9999,
				WithJobOffer:   false,
				IsCompleted:    nil,
			},
			additionalIDs: []int{getExistCourseID()},
			expErr:        false,
		},
		{
			name: "specified many courses",
			newCourse: Course{
				CourseName:     uuid.NewString(),
				CoursePrice:    888,
				Description:    "Only python develop another python developer",
				TopicName:      "MANAGEMENT",
				CourseDuration: 9999,
				WithJobOffer:   false,
				IsCompleted:    nil,
			},
			additionalIDs: func() []int {
				courses, err := getAllCourses()
				require.NoError(t, err)
				data := make([]int, 0, len(courses))
				for _, cours := range courses {
					data = append(data, cours.CourseID)
				}
				return data
			}(),
			expErr: false,
		},
		{
			name: "invalid course specified",
			newCourse: Course{
				CourseName:     uuid.NewString(),
				CoursePrice:    888,
				Description:    "Only python develop another python developer",
				TopicName:      "MANAGEMENT",
				CourseDuration: 9999,
				WithJobOffer:   false,
				IsCompleted:    nil,
			},
			additionalIDs: []int{-1, -2},
			expErr:        true,
		},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			token, err := generateToken("admin@admin.admin", "admin")
			require.NoError(t, err)

			err = createCourse(token, tc.newCourse)
			require.NoError(t, err, "failed to create course")
			createdCourse, err := getCourseByName(tc.newCourse.CourseName)
			require.NoError(t, err, "failed to get just created course")
			for _, val := range tc.additionalIDs {
				updatedCourse, err := addAdditionalCourse(token, createdCourse.CourseID, val)
				if tc.expErr {
					require.Error(t, err, tc.name)
				} else {
					require.NoError(t, err, tc.name)
					require.Equal(t, updatedCourse.CourseName, tc.newCourse.CourseName)
				}
			}

		})
	}
}

func TestUpdateCourse(t *testing.T) {

	courseToUpdate := func() Course {
		courses, err := getAllCourses()
		require.NoError(t, err)
		require.True(t, len(courses) > 0, "0 courses in the system")
		return courses[0]
	}()

	testCases := []struct {
		name       string
		course     Course
		toUpdate   Course
		toUpdateID int
		expErr     bool
	}{
		{
			name: "successful creation",
			course: Course{
				CourseName:     "skillbox",
				CoursePrice:    2341,
				Description:    "cool text of bullshit",
				TopicName:      "MARKETING",
				CourseDuration: 23,

				WithJobOffer: false,
			},
			toUpdate:   courseToUpdate,
			toUpdateID: courseToUpdate.CourseID,
			expErr:     false,
		},
		{
			name: "update course, which not exist",
			course: Course{
				CourseName:     "skillbox",
				CoursePrice:    2341,
				Description:    "cool text of bullshit",
				TopicName:      "MARKETING",
				CourseDuration: 23,

				WithJobOffer: false,
			},
			toUpdate: Course{
				CourseName:     "skillbox",
				CoursePrice:    2341,
				Description:    "cool text of bullshit",
				TopicName:      "MARKETING",
				CourseDuration: 23,

				WithJobOffer: false,
			},
			toUpdateID: -5,
			expErr:     true,
		},
	}
	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			token, err := generateToken("admin@admin.admin", "admin")
			require.NoError(t, err)

			err = updateCourse(token, tc.course, tc.toUpdateID)
			if tc.expErr {
				require.Error(t, err)
			} else {
				t.Logf("was updated course %v", tc.toUpdateID)
				require.NoError(t, err)
			}
		})
	}
}

func TestEnrollUser(t *testing.T) {
	t.Run("simple enrollment", func(t *testing.T) {
		regB := RegistrationBody{
			FirstName:   uuid.NewString(),
			LastName:    uuid.NewString(),
			Email:       uuid.NewString() + "@gmail.com",
			Password:    uuid.NewString(),
			PhoneNumber: "+83212313122",
			CompanyName: uuid.NewString(),
		}
		_, err := signUp(regB)
		require.NoError(t, err)

		courseID := getExistCourseID()

		token, err := generateToken("admin@admin.admin", "admin")
		require.NoError(t, err)

		err = enrollUser(token, 1, courseID)
		require.NoError(t, err)
	})

	t.Run("enroll on unexistent course", func(t *testing.T) {
		regB := RegistrationBody{
			FirstName:   uuid.NewString(),
			LastName:    uuid.NewString(),
			Email:       uuid.NewString() + "@gmail.com",
			Password:    uuid.NewString(),
			PhoneNumber: "+83212313122",
			CompanyName: uuid.NewString(),
		}
		_, err := signUp(regB)
		require.NoError(t, err)

		courseID := getExistCourseID()

		token, err := generateToken("admin@admin.admin", "admin")
		require.NoError(t, err)

		err = enrollUser(token, 1, -courseID)
		require.Error(t, err)
	})
}

func TestCreaeteModule(t *testing.T) {
	existCourseID := getExistCourseID()
	if existCourseID == 0 {
		t.Skip("no existence course")
	}

	testCases := []struct {
		name   string
		module ModuleEntity
		expErr bool
	}{
		//{ TODO вырублен, тк ordernum защита мешается быть тестам перезапускаемыми
		//	name: "full valid test",
		//	module: ModuleEntity{
		//		Name:        "module",
		//		IsCompleted: false,
		//		OrderNumber: 521,
		//		Description: "module descpript",
		//		Course: struct {
		//			CourseId int `json:"courseId"`
		//		}{
		//			CourseId: getExistCourseID(),
		//		},
		//		ModuleExercises: nil,
		//	},
		//	expErr: false,
		//},
		{
			name: "invalid order test",
			module: ModuleEntity{
				Name:        "module",
				IsCompleted: false,
				OrderNumber: -1,
				Description: "module descpript",
				Course: struct {
					CourseId int `json:"courseId"`
				}{
					CourseId: getExistCourseID(),
				},
				ModuleExercises: nil,
			},
			expErr: true,
		},
		{
			name: "course which is not exist",
			module: ModuleEntity{
				Name:        "module",
				IsCompleted: false,
				OrderNumber: 1,
				Description: "module descpript",
				Course: struct {
					CourseId int `json:"courseId"`
				}{
					CourseId: -2,
				},
				ModuleExercises: nil,
			},
			expErr: true,
		},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			token, err := generateToken("admin@admin.admin", "admin")
			require.NoError(t, err)

			err = createModule(token, tc.module)
			modules, moduleErr := getModules(token, existCourseID)
			require.NoError(t, moduleErr)
			t.Log("got results")
			fmt.Println(modules)
			if tc.expErr {
				require.Error(t, err)
			} else {
				require.NoError(t, err)
			}
		})
	}
}
