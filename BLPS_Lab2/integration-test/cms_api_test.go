package data

import (
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
