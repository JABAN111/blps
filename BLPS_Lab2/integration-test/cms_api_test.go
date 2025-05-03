package data

import (
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
				CourseName:     "Random bullshit from skillbox",
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
